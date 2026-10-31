package com.system.design.moviebooking.service;

import com.system.design.moviebooking.dto.SeatMapDto;
import com.system.design.moviebooking.dto.ShowRequestDto;
import com.system.design.moviebooking.dto.ShowResponseDto;
import com.system.design.moviebooking.dto.TheatreShowsDto;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface IShowService {
    ShowResponseDto createShow(ShowRequestDto request, Long callerId);
    List<TheatreShowsDto> getShowsForMovie(Long movieId, LocalDate date);
    SeatMapDto getSeatMap(Long showId);
    List<ShowResponseDto> getShowsOfTheatre(Long theatreId, Long callerId, Pageable pageable);
}
