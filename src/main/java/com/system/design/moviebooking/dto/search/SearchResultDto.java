package com.system.design.moviebooking.dto.search;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SearchResultDto {
    private Long showId;
    private LocalDateTime startTime;
    private BigDecimal ticketPrice;
    private Long movieId;
    private String movieTitle;
    private String poster;
    private String genre;
    private String language;
    private Long theatreId;
    private String theatreName;
    private String city;
}

