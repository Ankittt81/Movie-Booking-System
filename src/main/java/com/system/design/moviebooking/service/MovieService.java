package com.system.design.moviebooking.service;

import com.system.design.moviebooking.dto.MovieRequestDto;
import com.system.design.moviebooking.dto.MovieResponseDto;
import com.system.design.moviebooking.model.mainEntity.Movie;
import com.system.design.moviebooking.repository.MovieRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService implements IMovieService{
    private MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }
    @Override
    public List<MovieResponseDto> getMovies(String title, Pageable pageable) {
       return movieRepository.findByActiveTrueAndTitleContainingIgnoreCase(title.trim(),pageable)
                .map(this::toDto)
                .getContent();
    }

    @Override
    public MovieResponseDto getMovie(Long movieId) {
        Movie movie= movieRepository.findByIdAndActiveTrue(movieId);
        return toDto(movie);
    }

    @Override
    public MovieResponseDto createMovie(MovieRequestDto movieRequestDto) {
        Movie movie = new Movie();
        movie.setTitle(movieRequestDto.getTitle());
        movie.setDescription(movieRequestDto.getDescription());
        movie.setReleaseDate(movieRequestDto.getReleaseDate());
        movie.setPoster(movieRequestDto.getPoster());
        movie.setGenre(movieRequestDto.getGenre());
        movie.setLanguage(movieRequestDto.getLanguage());
        movie.setDuration(movieRequestDto.getDuration());
        movieRepository.save(movie);

        return toDto(movie);
    }

    @Override
    public MovieResponseDto updateMovie(Long movieId, MovieRequestDto movieRequestDto) {
        Movie movie= movieRepository.findByIdAndActiveTrue(movieId);
        movie.setTitle(movieRequestDto.getTitle());
        movie.setDescription(movieRequestDto.getDescription());
        movie.setReleaseDate(movieRequestDto.getReleaseDate());
        movie.setPoster(movieRequestDto.getPoster());
        movie.setGenre(movieRequestDto.getGenre());
        movie.setLanguage(movieRequestDto.getLanguage());
        movie.setDuration(movieRequestDto.getDuration());
        movieRepository.save(movie);
        return toDto(movie);
    }

    @Override
    public void deleteMovie(Long movieId) {
        movieRepository.deleteById(movieId);
    }

    public MovieResponseDto toDto(Movie movie) {
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
