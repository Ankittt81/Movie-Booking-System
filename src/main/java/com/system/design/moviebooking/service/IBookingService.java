package com.system.design.moviebooking.service;

import com.system.design.moviebooking.dto.booking.BookingResponseDto;
import com.system.design.moviebooking.dto.booking.HoldSeatsRequestDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IBookingService {
    // Phase 1: lock the seats and hold them for a few minutes
    BookingResponseDto holdSeats(HoldSeatsRequestDto request, Long userId);

    // Phase 2: pay, then turn the held seats into booked seats
    BookingResponseDto confirm(Long bookingId, Long userId);

    // Release the seats; allowed for PENDING and SUCCESSFUL bookings before the show starts
    BookingResponseDto cancel(Long bookingId, Long userId);

    List<BookingResponseDto> getMyBookings(Long userId, Pageable pageable);
}
