package com.system.design.moviebooking.dto;

import com.system.design.moviebooking.model.enums.BookingStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class BookingResponseDto {
   private Long bookingId;
   private BookingStatus status;
   private Long showId;
   private String  movieTitle;
   private String  theatreName;
   private LocalDateTime startTime;
   private List<String> seatLabels;
   private BigDecimal totalAmount;
   private LocalDateTime expiresAt;
   private String paymentRef;
}
