package com.system.design.moviebooking.service;

import com.system.design.moviebooking.dto.movieAndTheatre.MovieRequestDto;
import com.system.design.moviebooking.dto.movieAndTheatre.MovieResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IMovieService {
    List<MovieResponseDto> getMovies(String title, Pageable pageable);
    MovieResponseDto getMovie(Long movieId);
    MovieResponseDto createMovie(MovieRequestDto movieRequestDto);
    MovieResponseDto updateMovie(Long movieId, MovieRequestDto movieRequestDto);
    void deleteMovie(Long movieId);

}
