package com.system.design.moviebooking.controller;

import com.system.design.moviebooking.dto.ApiResponse;
import com.system.design.moviebooking.dto.movieAndTheatre.MovieRequestDto;
import com.system.design.moviebooking.dto.movieAndTheatre.MovieResponseDto;
import com.system.design.moviebooking.service.IMovieService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {
    private IMovieService movieService;

    public MovieController(IMovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<MovieResponseDto>>> getAllMovies(@RequestParam String title, @RequestParam Integer page, @RequestParam Integer pageSize) {
        List<MovieResponseDto> response=movieService.getMovies(title, PageRequest.of(page, pageSize));
        return ResponseEntity.ok().body(new ApiResponse<List<MovieResponseDto>>("Fetched movies",response));
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<ApiResponse<MovieResponseDto>> getMovie(@PathVariable Long movieId) {
        MovieResponseDto response=movieService.getMovie(movieId);
        return ResponseEntity.ok().body(new ApiResponse<>("Fetched movie",response));
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<MovieResponseDto>> createMovie(@RequestBody MovieRequestDto movieRequestDto) {
        MovieResponseDto response=movieService.createMovie(movieRequestDto);
        return ResponseEntity.ok().body(new ApiResponse<>("Created movie",response));
    }

    @PutMapping("/{movieId}")
    public ResponseEntity<ApiResponse<MovieResponseDto>> updateMovie(@PathVariable Long movieId, @RequestBody MovieRequestDto movieRequestDto) {
        MovieResponseDto response=movieService.updateMovie(movieId, movieRequestDto);
        return ResponseEntity.ok().body(new ApiResponse<>("Updated movie",response));
    }

    @DeleteMapping("/{movieId}")
    public ResponseEntity<ApiResponse<MovieResponseDto>> deleteMovie(@PathVariable Long movieId) {
        movieService.deleteMovie(movieId);
        return ResponseEntity.ok().body(new ApiResponse<>("Deleted movie",null));
    }
}
