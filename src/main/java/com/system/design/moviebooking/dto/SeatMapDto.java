package com.system.design.moviebooking.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class SeatMapDto {
    private Long showId;
    private String movieTitle;
    private String theatreName;
    private String theatreAddress;
    private LocalDateTime startTime;
    private BigDecimal ticketPrice;
    private int totalSeats;
    private int availableSeats;
    private List<SeatDto> seats;
}
