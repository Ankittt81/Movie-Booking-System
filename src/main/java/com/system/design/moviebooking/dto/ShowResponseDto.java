package com.system.design.moviebooking.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ShowResponseDto {
    private Long id;
    private Long movieId;
    private String movieTitle;
    private Long theatreId;
    private String theatreName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal ticketPrice;
    private int rowCount;
    private int seatsPerRow;
    private int totalSeats;

    public ShowResponseDto(Long id, Long id1, String title, Long id2, String name, LocalDateTime startTime, LocalDateTime endTime, BigDecimal ticketPrice, int rowCount, int seatsPerRow, int i) {
        this.id = id;
        this.movieId = id1;
        this.movieTitle = title;
        this.theatreId = id2;
        this.theatreName = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.ticketPrice = ticketPrice;
        this.rowCount = rowCount;
        this.seatsPerRow = seatsPerRow;
        this.totalSeats = i;

    }
}
