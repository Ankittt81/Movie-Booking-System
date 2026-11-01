package com.system.design.moviebooking.controller;

import com.system.design.moviebooking.dto.ApiResponse;
import com.system.design.moviebooking.dto.movieAndTheatre.TheatreRequestDto;
import com.system.design.moviebooking.dto.movieAndTheatre.TheatreResponseDto;
import com.system.design.moviebooking.dto.movieAndTheatre.TheatreStatusRequestDto;
import com.system.design.moviebooking.model.enums.TheatreStatus;
import com.system.design.moviebooking.security.AuthUser;
import com.system.design.moviebooking.service.ITheatreService;
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
public class TheatreController {

    private static final int MAX_PAGE_SIZE = 50;

    private final ITheatreService theatreService;

    public TheatreController(ITheatreService theatreService) {
        this.theatreService = theatreService;
    }

    // ---------- Partner endpoints ----------

    @PostMapping("/theatres")
    @PreAuthorize("hasRole('PARTNER')")
    public ResponseEntity<ApiResponse<TheatreResponseDto>> createTheatre(
            @Valid @RequestBody TheatreRequestDto request,
            @AuthenticationPrincipal AuthUser authUser) {

        TheatreResponseDto response = theatreService.create(request, authUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Theatre created, pending approval", response));
    }

    @GetMapping("/theatres/my")
    @PreAuthorize("hasRole('PARTNER')")
    public ResponseEntity<ApiResponse<List<TheatreResponseDto>>> getMyTheatres(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        List<TheatreResponseDto> response =
                theatreService.getMyTheatres(authUser.getId(), pageable(page, pageSize));
        return ResponseEntity.ok(new ApiResponse<>("Fetched your theatres", response));
    }

    // Ownership is checked inside the service (403 if the caller is not the owner)
    @PutMapping("/theatres/{theatreId}")
    @PreAuthorize("hasRole('PARTNER')")
    public ResponseEntity<ApiResponse<TheatreResponseDto>> updateTheatre(
            @PathVariable Long theatreId,
            @Valid @RequestBody TheatreRequestDto request,
            @AuthenticationPrincipal AuthUser authUser) {

        TheatreResponseDto response = theatreService.update(theatreId, request, authUser.getId());
        return ResponseEntity.ok(new ApiResponse<>("Theatre updated", response));
    }

    // ---------- Admin endpoints ----------

    @GetMapping("/admin/theatres")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TheatreResponseDto>>> getTheatresByStatus(
            @RequestParam(defaultValue = "PENDING") TheatreStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        List<TheatreResponseDto> response =
                theatreService.getByStatus(status, pageable(page, pageSize));
        return ResponseEntity.ok(new ApiResponse<>("Fetched theatres", response));
    }

    @PutMapping("/admin/theatres/{theatreId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TheatreResponseDto>> changeStatus(
            @PathVariable Long theatreId,
            @Valid @RequestBody TheatreStatusRequestDto request) {

        TheatreResponseDto response = theatreService.changeStatus(theatreId, request.getStatus());
        return ResponseEntity.ok(new ApiResponse<>("Theatre status updated", response));
    }

    // ---------- helper ----------

    // Guards against negative page and absurd sizes (PageRequest.of throws on bad values)
    private Pageable pageable(int page, int pageSize) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(pageSize, 1), MAX_PAGE_SIZE);
        return PageRequest.of(safePage, safeSize);
    }
}