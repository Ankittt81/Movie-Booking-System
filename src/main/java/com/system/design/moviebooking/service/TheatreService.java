package com.system.design.moviebooking.service;

import com.system.design.moviebooking.dto.TheatreRequestDto;
import com.system.design.moviebooking.dto.TheatreResponseDto;
import com.system.design.moviebooking.model.enums.TheatreStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TheatreService implements ITheatreService{
    @Override
    public TheatreResponseDto create(TheatreRequestDto request, Long ownerId) {
        return null;
    }

    @Override
    public List<TheatreResponseDto> getMyTheatres(Long ownerId, Pageable pageable) {
        return List.of();
    }

    @Override
    public TheatreResponseDto update(Long theatreId, TheatreRequestDto request, Long callerId) {
        return null;
    }

    @Override
    public List<TheatreResponseDto> getByStatus(TheatreStatus request, Pageable pageable) {
        return List.of();
    }

    @Override
    public TheatreResponseDto changeStatus(Long theatreId, TheatreStatus newStatus) {
        return null;
    }
}
