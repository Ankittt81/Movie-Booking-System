package com.system.design.moviebooking.service;

import com.system.design.moviebooking.dto.movieAndTheatre.TheatreShowsDto;
import com.system.design.moviebooking.dto.seatAndShow.*;
import com.system.design.moviebooking.exceptions.AccessDeniedException;
import com.system.design.moviebooking.exceptions.BusinessException;
import com.system.design.moviebooking.exceptions.ResourceNotFound;
import com.system.design.moviebooking.model.enums.SeatStatus;
import com.system.design.moviebooking.model.enums.TheatreStatus;
import com.system.design.moviebooking.model.mainEntity.*;
import com.system.design.moviebooking.repository.*;
import org.springframework.data.domain.Pageable;
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
    @Transactional
    public ShowResponseDto createShow(ShowRequestDto request, Long callerId) {
        Theatre theatre = getOwnedTheatre(request.getTheatreId(), callerId);

        if (theatre.getStatus() != TheatreStatus.ACTIVE) {
            throw new BusinessException("Theatre is not approved yet");
        }

        Movie movie = movieRepository.findByIdAndActiveTrue(request.getMovieId());
        if (movie == null) {
            throw new ResourceNotFound("Movie not found");
        }

        Show show = new Show();
        show.setMovie(movie);
        show.setTheatre(theatre);
        show.setStartTime(request.getStartTime());
        show.setEndTime(request.getStartTime().plusMinutes(movie.getDuration()));
        show.setTicketPrice(request.getTicketPrice());
        show.setRowCount(request.getRowCount());
        show.setSeatsPerRow(request.getSeatsPerRow());
        Show saved = showRepository.save(show);

        // same transaction: a show never exists without its seats
        showSeatRepository.saveAll(
                generateSeats(saved, request.getRowCount(), request.getSeatsPerRow()));

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TheatreShowsDto> getShowsForMovie(Long movieId, LocalDate date) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime from = dayStart.isBefore(now) ? now : dayStart; // hide shows that already started
        LocalDateTime to = date.plusDays(1).atStartOfDay();

        List<Show> shows = showRepository.findShowsForMovie(movieId, from, to);

        // group by theatre, keeping the query's ordering
        return shows.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getTheatre().getId(),
                        LinkedHashMap::new,
                        Collectors.toList()))
                .values().stream()
                .map(group -> toTheatreShows(group, now))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SeatMapDto getSeatMap(Long showId) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new ResourceNotFound("Show not found"));

        LocalDateTime now = LocalDateTime.now();
        List<SeatDto> seats = showSeatRepository.findByShowIdOrderById(showId).stream()
                .map(s -> {
                    SeatDto dto = new SeatDto();
                    dto.setLabel(s.getSeatLabel());
                    dto.setStatus(effectiveStatus(s, now));
                    return dto;
                })
                .toList();

        int available = (int) seats.stream()
                .filter(s -> s.getStatus() == SeatStatus.AVAILABLE)
                .count();

        SeatMapDto map = new SeatMapDto();
        map.setShowId(show.getId());
        map.setMovieTitle(show.getMovie().getTitle());
        map.setTheatreName(show.getTheatre().getName());
        map.setTheatreAddress(show.getTheatre().getAddress());
        map.setStartTime(show.getStartTime());
        map.setTicketPrice(show.getTicketPrice());
        map.setTotalSeats(seats.size());
        map.setAvailableSeats(available);
        map.setSeats(seats);
        return map;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShowResponseDto> getShowsOfTheatre(Long theatreId, Long callerId, Pageable pageable) {
        getOwnedTheatre(theatreId, callerId);
        return showRepository.findByTheatreId(theatreId, pageable)
                .map(this::toResponse)
                .getContent();
    }

    // ---------------- helpers ----------------

    private Theatre getOwnedTheatre(Long theatreId, Long callerId) {
        Theatre theatre = theatreRepository.findById(theatreId)
                .orElseThrow(() -> new ResourceNotFound("Theatre not found"));
        if (!theatre.getOwner().getId().equals(callerId)) {
            throw new AccessDeniedException("You do not own this theatre");
        }
        return theatre;
    }

    private List<ShowSeat> generateSeats(Show show, int rowCount, int seatsPerRow) {
        List<ShowSeat> seats = new ArrayList<>(rowCount * seatsPerRow);
        for (int r = 0; r < rowCount; r++) {
            char rowLetter = (char) ('A' + r);
            for (int n = 1; n <= seatsPerRow; n++) {
                ShowSeat seat = new ShowSeat();
                seat.setShow(show);
                seat.setSeatLabel(rowLetter + String.valueOf(n)); // A1, A2 ... B1 ...
                seat.setStatus(SeatStatus.AVAILABLE);
                seats.add(seat);
            }
        }
        return seats;
    }

    // a HELD seat whose hold has expired is shown as AVAILABLE, even before cleanup runs
    private SeatStatus effectiveStatus(ShowSeat seat, LocalDateTime now) {
        if (seat.getStatus() == SeatStatus.HELD
                && seat.getHeldUntil() != null
                && seat.getHeldUntil().isBefore(now)) {
            return SeatStatus.AVAILABLE;
        }
        return seat.getStatus();
    }

    private TheatreShowsDto toTheatreShows(List<Show> group, LocalDateTime now) {
        Theatre t = group.get(0).getTheatre();
        List<ShowSummaryDto> summaries = group.stream()
                .map(s -> new ShowSummaryDto(
                        s.getId(),
                        s.getStartTime(),
                        s.getTicketPrice(),
                        showSeatRepository.countAvailable(s.getId(), now)))
                .toList();

        TheatreShowsDto dto = new TheatreShowsDto();
        dto.setTheatreId(t.getId());
        dto.setTheatreName(t.getName());
        dto.setAddress(t.getAddress());
        dto.setCity(t.getCity());
        dto.setShows(summaries);
        return dto;
    }

    private ShowResponseDto toResponse(Show s) {
        return new ShowResponseDto(
                s.getId(),
                s.getMovie().getId(),
                s.getMovie().getTitle(),
                s.getTheatre().getId(),
                s.getTheatre().getName(),
                s.getStartTime(),
                s.getEndTime(),
                s.getTicketPrice(),
                s.getRowCount(),
                s.getSeatsPerRow(),
                s.getRowCount() * s.getSeatsPerRow());
    }
}