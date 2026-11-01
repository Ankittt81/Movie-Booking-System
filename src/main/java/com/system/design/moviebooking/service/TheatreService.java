package com.system.design.moviebooking.service;

import com.system.design.moviebooking.dto.movieAndTheatre.TheatreRequestDto;
import com.system.design.moviebooking.dto.movieAndTheatre.TheatreResponseDto;

import com.system.design.moviebooking.exceptions.AccessDeniedException;
import com.system.design.moviebooking.exceptions.BusinessException;
import com.system.design.moviebooking.exceptions.ResourceNotFound;
import com.system.design.moviebooking.model.enums.TheatreStatus;
import com.system.design.moviebooking.model.mainEntity.Theatre;
import com.system.design.moviebooking.repository.TheatreRepository;
import com.system.design.moviebooking.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class TheatreService implements ITheatreService{
    private TheatreRepository  theatreRepository;
    private UserRepository  userRepository;

    public TheatreService(TheatreRepository theatreRepository, UserRepository userRepository) {
        this.theatreRepository = theatreRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public TheatreResponseDto create(TheatreRequestDto request, Long ownerId) {
        Theatre theatre = new Theatre();
        apply(request,theatre);
        theatre.setStatus(TheatreStatus.PENDING);
//        User owner=userRepository.findById(ownerId)
//                .orElseThrow(()->new AuthException("User not found"));
        theatre.setOwner(userRepository.getReferenceById(ownerId));
        return toDto(theatreRepository.save(theatre));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TheatreResponseDto> getMyTheatres(Long ownerId, Pageable pageable) {
        return theatreRepository.findByOwnerId(ownerId, pageable)
                .map(this::toDto)
                .getContent();
    }

    @Override
    @Transactional
    public TheatreResponseDto update(Long theatreId, TheatreRequestDto request, Long callerId) {
        Theatre theatre = findOrThrow(theatreId);
        if (!theatre.getOwner().getId().equals(callerId)) {
            throw new AccessDeniedException("You do not own this theatre");
        }
        apply(request,theatre);
        return toDto(theatre);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TheatreResponseDto> getByStatus(TheatreStatus request, Pageable pageable) {
        return theatreRepository.findByStatus(request, pageable)
                .map(this::toDto)
                .getContent();
    }

    @Override
    @Transactional
    public TheatreResponseDto changeStatus(Long theatreId, TheatreStatus newStatus) {
        Theatre theatre = findOrThrow(theatreId);
        if (theatre.getStatus() == newStatus) {
            throw new BusinessException("Theatre is already " + newStatus);
        }
        if (newStatus == TheatreStatus.PENDING) {
            throw new BusinessException("A theatre cannot go back to PENDING");
        }
        theatre.setStatus(newStatus);
        return toDto(theatre);
    }

    private void apply(TheatreRequestDto request, Theatre theatre) {
        theatre.setAddress(request.getAddress());
        theatre.setCity(request.getCity());
        theatre.setEmail(request.getEmail());
        theatre.setPhone(request.getPhone());
        theatre.setName(request.getName());
    }

    private TheatreResponseDto toDto(Theatre theatre) {
        TheatreResponseDto dto = new TheatreResponseDto();
        dto.setId(theatre.getId());
        dto.setAddress(theatre.getAddress());
        dto.setCity(theatre.getCity());
        dto.setEmail(theatre.getEmail());
        dto.setPhone(theatre.getPhone());
        dto.setName(theatre.getName());
        dto.setStatus(theatre.getStatus());
        dto.setOwnerId(theatre.getOwner().getId());
        dto.setOwnerName(theatre.getOwner().getName());
        return dto;
    }

    private Theatre findOrThrow(Long id){
        return theatreRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Theatre not found"));
    }
}
