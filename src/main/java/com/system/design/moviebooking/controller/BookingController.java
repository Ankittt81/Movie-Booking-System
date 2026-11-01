package com.system.design.moviebooking.controller;

import com.system.design.moviebooking.dto.ApiResponse;
import com.system.design.moviebooking.dto.booking.BookingResponseDto;
import com.system.design.moviebooking.dto.booking.HoldSeatsRequestDto;
import com.system.design.moviebooking.security.AuthUser;
import com.system.design.moviebooking.service.IBookingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@PreAuthorize("hasRole('USER')")
public class BookingController {

    private static final int MAX_PAGE_SIZE = 50;

    private final IBookingService bookingService;

    public BookingController(IBookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Phase 1: lock and hold the seats for a few minutes
    @PostMapping("/hold")
    public ResponseEntity<ApiResponse<BookingResponseDto>> holdSeats(
            @Valid @RequestBody HoldSeatsRequestDto request,
            @AuthenticationPrincipal AuthUser authUser) {

        BookingResponseDto response = bookingService.holdSeats(request, authUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Seats held, please complete payment before the hold expires", response));
    }

    // Phase 2: pay and confirm
    @PostMapping("/{bookingId}/confirm")
    public ResponseEntity<ApiResponse<BookingResponseDto>> confirm(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal AuthUser authUser) {

        BookingResponseDto response = bookingService.confirm(bookingId, authUser.getId());
        return ResponseEntity.ok(new ApiResponse<>("Booking confirmed", response));
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<ApiResponse<BookingResponseDto>> cancel(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal AuthUser authUser) {

        BookingResponseDto response = bookingService.cancel(bookingId, authUser.getId());
        return ResponseEntity.ok(new ApiResponse<>("Booking cancelled", response));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<BookingResponseDto>>> getMyBookings(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        List<BookingResponseDto> response =
                bookingService.getMyBookings(authUser.getId(), pageable(page, pageSize));
        return ResponseEntity.ok(new ApiResponse<>("Fetched your bookings", response));
    }

    // ---------- helper ----------

    private Pageable pageable(int page, int pageSize) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(pageSize, 1), MAX_PAGE_SIZE);
        return PageRequest.of(safePage, safeSize, org.springframework.data.domain.Sort.by("id").descending());
    }
}