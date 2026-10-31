package com.system.design.moviebooking.service;

import com.system.design.moviebooking.dto.*;
import com.system.design.moviebooking.exceptions.BusinessException;
import com.system.design.moviebooking.model.enums.SeatStatus;
import com.system.design.moviebooking.model.enums.TheatreStatus;
import com.system.design.moviebooking.model.mainEntity.*;
import com.system.design.moviebooking.repository.*;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShowService implements IShowService {

    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;
    private final TheatreRepository theatreRepository;
    private final MovieRepository movieRepository;

    public ShowService(ShowRepository showRepository,
                       ShowSeatRepository showSeatRepository,
                       TheatreRepository theatreRepository,
                       MovieRepository movieRepository) {
        this.showRepository = showRepository;
        this.showSeatRepository = showSeatRepository;
        this.theatreRepository = theatreRepository;
        this.movieRepository = movieRepository;
    }


    @Override
    public ShowResponseDto createShow(ShowRequestDto request, Long callerId) {
        return null;
    }

    @Override
    public List<TheatreShowsDto> getShowsForMovie(Long movieId, LocalDate date) {
        return List.of();
    }

    @Override
    public SeatMapDto getSeatMap(Long showId) {
        return null;
    }

    @Override
    public List<ShowResponseDto> getShowsOfTheatre(Long theatreId, Long callerId, Pageable pageable) {
        return List.of();
    }
}