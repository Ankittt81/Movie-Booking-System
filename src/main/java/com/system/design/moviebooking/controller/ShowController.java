package com.system.design.moviebooking.controller;

import com.system.design.moviebooking.dto.ApiResponse;
import com.system.design.moviebooking.dto.SeatMapDto;
import com.system.design.moviebooking.dto.ShowRequestDto;
import com.system.design.moviebooking.dto.ShowResponseDto;
import com.system.design.moviebooking.dto.TheatreShowsDto;
import com.system.design.moviebooking.security.AuthUser;
import com.system.design.moviebooking.service.IShowService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class ShowController {

    private static final int MAX_PAGE_SIZE = 50;

    private final IShowService showService;

    public ShowController(IShowService showService) {
        this.showService = showService;
    }

    // ---------- Partner endpoints ----------

    @PostMapping("/shows")
    @PreAuthorize("hasRole('PARTNER')")
    public ResponseEntity<ApiResponse<ShowResponseDto>> createShow(
            @Valid @RequestBody ShowRequestDto request,
            @AuthenticationPrincipal AuthUser authUser) {

        ShowResponseDto response = showService.createShow(request, authUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Show created", response));
    }

    // Ownership is checked inside the service (403 if the caller does not own the theatre)
    @GetMapping("/theatres/{theatreId}/shows")
    @PreAuthorize("hasRole('PARTNER')")
    public ResponseEntity<ApiResponse<List<ShowResponseDto>>> getShowsOfTheatre(
            @PathVariable Long theatreId,
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        List<ShowResponseDto> response =
                showService.getShowsOfTheatre(theatreId, authUser.getId(), pageable(page, pageSize));
        return ResponseEntity.ok(new ApiResponse<>("Fetched shows", response));
    }

    // ---------- Customer endpoints (any logged-in user) ----------

    // GET /shows?movieId=1&date=2026-11-05   (date is optional, defaults to today)
    @GetMapping("/shows")
    public ResponseEntity<ApiResponse<List<TheatreShowsDto>>> getShowsForMovie(
            @RequestParam Long movieId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate day = (date != null) ? date : LocalDate.now();
        List<TheatreShowsDto> response = showService.getShowsForMovie(movieId, day);
        return ResponseEntity.ok(new ApiResponse<>("Fetched shows", response));
    }

    @GetMapping("/shows/{showId}/seats")
    public ResponseEntity<ApiResponse<SeatMapDto>> getSeatMap(@PathVariable Long showId) {
        SeatMapDto response = showService.getSeatMap(showId);
        return ResponseEntity.ok(new ApiResponse<>("Fetched seat map", response));
    }

    // ---------- helper ----------

    private Pageable pageable(int page, int pageSize) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(pageSize, 1), MAX_PAGE_SIZE);
        return PageRequest.of(safePage, safeSize);
    }
}