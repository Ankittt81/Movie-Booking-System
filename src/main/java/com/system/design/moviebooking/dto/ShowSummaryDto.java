package com.system.design.moviebooking.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ShowSummaryDto {
   private Long id;
   private LocalDateTime startTime;
   private BigDecimal ticketPrice;
   private long availableSeats;

    public ShowSummaryDto(Long id, LocalDateTime startTime, BigDecimal ticketPrice, long available) {
        this.id = id;
        this.startTime = startTime;
        this.ticketPrice = ticketPrice;
        this.availableSeats=available;
    }
}
