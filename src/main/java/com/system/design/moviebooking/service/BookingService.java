package com.system.design.moviebooking.service;

import com.system.design.moviebooking.dto.booking.BookingResponseDto;
import com.system.design.moviebooking.dto.booking.HoldSeatsRequestDto;
import com.system.design.moviebooking.dto.PaymentResult;
import com.system.design.moviebooking.exceptions.AccessDeniedException;
import com.system.design.moviebooking.exceptions.BusinessException;
import com.system.design.moviebooking.exceptions.ResourceNotFound;
import com.system.design.moviebooking.model.enums.BookingStatus;
import com.system.design.moviebooking.model.enums.SeatStatus;
import com.system.design.moviebooking.model.mainEntity.Booking;
import com.system.design.moviebooking.model.mainEntity.Show;
import com.system.design.moviebooking.model.mainEntity.ShowSeat;
import com.system.design.moviebooking.repository.BookingRepository;
import com.system.design.moviebooking.repository.ShowRepository;
import com.system.design.moviebooking.repository.ShowSeatRepository;
import com.system.design.moviebooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookingService implements IBookingService {

    private final BookingRepository bookingRepository;
    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;
    private final UserRepository userRepository;
    private final IPaymentService paymentService;
    private final int holdMinutes;

    public BookingService(BookingRepository bookingRepository,
                          ShowRepository showRepository,
                          ShowSeatRepository showSeatRepository,
                          UserRepository userRepository,
                          IPaymentService paymentService,
                          @Value("${booking.hold-minutes:5}") int holdMinutes) {
        this.bookingRepository = bookingRepository;
        this.showRepository = showRepository;
        this.showSeatRepository = showSeatRepository;
        this.userRepository = userRepository;
        this.paymentService = paymentService;
        this.holdMinutes = holdMinutes;
    }

    // ---------------------------------------------------------------
    // Phase 1: hold seats (pessimistic lock on the seat rows)
    // ---------------------------------------------------------------
    @Override
    @Transactional
    public BookingResponseDto holdSeats(HoldSeatsRequestDto request, Long userId) {
        Show show = showRepository.findById(request.getShowId())
                .orElseThrow(() -> new ResourceNotFound("Show not found"));

        LocalDateTime now = LocalDateTime.now();
        if (!show.getStartTime().isAfter(now)) {
            throw new BusinessException("Show has already started");
        }

        // normalize and remove duplicates: "a1" and "A1" count once
        Set<String> labels = request.getSeatLabels().stream()
                .map(l -> l.trim().toUpperCase())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // SELECT ... FOR UPDATE, ordered by id so two users can never deadlock each other
        List<ShowSeat> seats = showSeatRepository.findSeatsForUpdate(show.getId(), labels);

        if (seats.size() != labels.size()) {
            throw new BusinessException("One or more seats do not exist for this show");
        }
        for (ShowSeat seat : seats) {
            if (!isFree(seat, now)) {
                throw new BusinessException("Seat " + seat.getSeatLabel() + " is not available");
            }
        }

        Booking booking = new Booking();
        booking.setUser(userRepository.getReferenceById(userId));
        booking.setShow(show);
        booking.setStatus(BookingStatus.PENDING);
        booking.setTotalAmount(show.getTicketPrice().multiply(BigDecimal.valueOf(labels.size())));
        booking.setExpiresAt(now.plusMinutes(holdMinutes));
        bookingRepository.save(booking);

        // seats are managed entities: Hibernate writes these changes on commit
        for (ShowSeat seat : seats) {
            seat.setStatus(SeatStatus.HELD);
            seat.setHeldUntil(booking.getExpiresAt());
            seat.setBooking(booking);
        }

        return toResponse(booking, List.copyOf(labels));
    }

    // ---------------------------------------------------------------
    // Phase 2: confirm (payment, then HELD -> BOOKED)
    // ---------------------------------------------------------------
    @Override
    @Transactional
    public BookingResponseDto confirm(Long bookingId, Long userId) {
        Booking booking = getOwnedBookingForUpdate(bookingId, userId);
        LocalDateTime now = LocalDateTime.now();

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BusinessException("Booking is not pending");
        }
        if (booking.getExpiresAt().isBefore(now)) {
            throw new BusinessException("Seat hold expired, please select seats again");
        }

        // Fine for a mock. With a real gateway, charge outside this lock and make it idempotent.
        PaymentResult payment = paymentService.pay(booking.getId(), booking.getTotalAmount());
        if (!payment.isSuccess()) {
            throw new BusinessException("Payment failed");
        }

        long expected = showSeatRepository.countByBookingId(booking.getId());
        int booked = showSeatRepository.markBooked(booking.getId(), SeatStatus.BOOKED, SeatStatus.HELD);
        if (booked != expected) {
            // the hold was lost while paying; a real system would refund here
            throw new BusinessException("Seats are no longer held, payment will be refunded");
        }

        booking.setStatus(BookingStatus.SUCCESSFUL);
        booking.setPaymentRef(payment.getReference());
        return toResponse(booking, labelsOf(booking.getId()));
    }

    // ---------------------------------------------------------------
    // Cancel
    // ---------------------------------------------------------------
    @Override
    @Transactional
    public BookingResponseDto cancel(Long bookingId, Long userId) {
        Booking booking = getOwnedBookingForUpdate(bookingId, userId);

        if (booking.getStatus() != BookingStatus.PENDING
                && booking.getStatus() != BookingStatus.SUCCESSFUL) {
            throw new BusinessException("Booking cannot be cancelled");
        }
        if (!booking.getShow().getStartTime().isAfter(LocalDateTime.now())) {
            throw new BusinessException("Show has already started");
        }

        List<String> labels = labelsOf(booking.getId());   // read before the seats are released
        showSeatRepository.releaseByBooking(booking.getId(), SeatStatus.AVAILABLE);
        booking.setStatus(BookingStatus.CANCELLED);
        // refund for SUCCESSFUL bookings is a stub for now
        return toResponse(booking, labels);
    }

    // ---------------------------------------------------------------
    // My bookings
    // ---------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getMyBookings(Long userId, Pageable pageable) {
        return bookingRepository.findByUserId(userId, pageable)
                .map(b -> toResponse(b, labelsOf(b.getId())))   // one small query per booking, fine for now
                .getContent();
    }

    // ---------------- helpers ----------------

    // free = never taken, or held by someone whose hold has run out
    private boolean isFree(ShowSeat seat, LocalDateTime now) {
        if (seat.getStatus() == SeatStatus.AVAILABLE) {
            return true;
        }
        return seat.getStatus() == SeatStatus.HELD
                && seat.getHeldUntil() != null
                && seat.getHeldUntil().isBefore(now);
    }

    private Booking getOwnedBookingForUpdate(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> new ResourceNotFound("Booking not found"));
        if (!booking.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("This booking does not belong to you");
        }
        return booking;
    }

    private List<String> labelsOf(Long bookingId) {
        return showSeatRepository.findByBookingIdOrderById(bookingId).stream()
                .map(ShowSeat::getSeatLabel)
                .toList();
    }

    private BookingResponseDto toResponse(Booking b, List<String> labels) {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setBookingId(b.getId());
        dto.setStatus(b.getStatus());
        dto.setShowId(b.getShow().getId());
        dto.setMovieTitle(b.getShow().getMovie().getTitle());
        dto.setTheatreName(b.getShow().getTheatre().getName());
        dto.setStartTime(b.getShow().getStartTime());
        dto.setSeatLabels(labels);
        dto.setTotalAmount(b.getTotalAmount());
        dto.setExpiresAt(b.getExpiresAt());
        dto.setPaymentRef(b.getPaymentRef());
        return dto;
    }
}