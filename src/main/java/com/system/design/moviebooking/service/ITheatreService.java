package com.system.design.moviebooking.service;

import com.system.design.moviebooking.dto.movieAndTheatre.TheatreRequestDto;
import com.system.design.moviebooking.dto.movieAndTheatre.TheatreResponseDto;
import com.system.design.moviebooking.model.enums.TheatreStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ITheatreService {
    TheatreResponseDto create(TheatreRequestDto request, Long ownerId);
    List<TheatreResponseDto> getMyTheatres(Long ownerId, Pageable pageable);
    TheatreResponseDto update(Long theatreId, TheatreRequestDto request, Long callerId);
   List<TheatreResponseDto> getByStatus(TheatreStatus request, Pageable pageable);
    TheatreResponseDto changeStatus(Long theatreId, TheatreStatus newStatus);
}
