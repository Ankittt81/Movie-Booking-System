package com.system.design.moviebooking.service;

import com.system.design.moviebooking.dto.movieAndTheatre.MovieRequestDto;
import com.system.design.moviebooking.dto.movieAndTheatre.MovieResponseDto;
import com.system.design.moviebooking.exceptions.ResourceNotFound;
import com.system.design.moviebooking.model.mainEntity.Movie;
import com.system.design.moviebooking.repository.MovieRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MovieService implements IMovieService {
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieResponseDto> getMovies(String title, Pageable pageable) {
        return movieRepository.findByActiveTrueAndTitleContainingIgnoreCase(title.trim(), pageable)
                .map(this::toDto)
                .getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public MovieResponseDto getMovie(Long movieId) {
        return toDto(findActiveOrThrow(movieId));
    }

    @Override
    @Transactional
    public MovieResponseDto createMovie(MovieRequestDto request) {
        Movie movie = new Movie();
        apply(request, movie);
        return toDto(movieRepository.save(movie));
    }

    @Override
    @Transactional
    public MovieResponseDto updateMovie(Long movieId, MovieRequestDto request) {
        Movie movie = findActiveOrThrow(movieId);
        apply(request, movie);
        return toDto(movie);
    }

    // soft delete: shows and bookings keep pointing at the movie
    @Override
    @Transactional
    public void deleteMovie(Long movieId) {
        Movie movie = findActiveOrThrow(movieId);
        movie.setActive(false);
    }

    private Movie findActiveOrThrow(Long movieId) {
        return movieRepository.findByIdAndActiveTrue(movieId)
                .orElseThrow(() -> new ResourceNotFound("Movie not found"));
    }

    private void apply(MovieRequestDto request, Movie movie) {
        movie.setTitle(request.getTitle());
        movie.setDescription(request.getDescription());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setPoster(request.getPoster());
        movie.setGenre(request.getGenre());
        movie.setLanguage(request.getLanguage());
        movie.setDuration(request.getDuration());
    }

    private MovieResponseDto toDto(Movie movie) {
        MovieResponseDto dto = new MovieResponseDto();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setDescription(movie.getDescription());
        dto.setGenre(movie.getGenre());
        dto.setDuration(movie.getDuration());
        dto.setPoster(movie.getPoster());
        dto.setLanguage(movie.getLanguage());
        dto.setReleaseDate(movie.getReleaseDate());
        return dto;
    }
}