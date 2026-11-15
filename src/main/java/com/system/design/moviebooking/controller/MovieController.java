package com.system.design.moviebooking.controller;

import com.system.design.moviebooking.dto.ApiResponse;
import com.system.design.moviebooking.dto.movieAndTheatre.MovieRequestDto;
import com.system.design.moviebooking.dto.movieAndTheatre.MovieResponseDto;
import com.system.design.moviebooking.service.IMovieService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private static final int MAX_PAGE_SIZE = 50;

    private final IMovieService movieService;

    public MovieController(IMovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MovieResponseDto>>> getAllMovies(
            @RequestParam(defaultValue = "") String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        List<MovieResponseDto> response = movieService.getMovies(title, pageable(page, pageSize));
        return ResponseEntity.ok(new ApiResponse<>("Fetched movies", response));
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<ApiResponse<MovieResponseDto>> getMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(new ApiResponse<>("Fetched movie", movieService.getMovie(movieId)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MovieResponseDto>> createMovie(
            @Valid @RequestBody MovieRequestDto request) {

        MovieResponseDto response = movieService.createMovie(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("Created movie", response));
    }

    @PutMapping("/{movieId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MovieResponseDto>> updateMovie(
            @PathVariable Long movieId,
            @Valid @RequestBody MovieRequestDto request) {

        return ResponseEntity.ok(new ApiResponse<>("Updated movie", movieService.updateMovie(movieId, request)));
    }

    @DeleteMapping("/{movieId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteMovie(@PathVariable Long movieId) {
        movieService.deleteMovie(movieId);
        return ResponseEntity.ok(new ApiResponse<>("Deleted movie", null));
    }

    private Pageable pageable(int page, int pageSize) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(pageSize, 1), MAX_PAGE_SIZE);
        return PageRequest.of(safePage, safeSize);
    }
}