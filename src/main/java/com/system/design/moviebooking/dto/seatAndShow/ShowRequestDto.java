package com.system.design.moviebooking.dto.seatAndShow;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ShowRequestDto {
    private Long movieId;
    private Long theatreId;
    @NotNull
    @Future
    private LocalDateTime startTime;
    @NotNull
    @Positive
    private BigDecimal ticketPrice;
    @Min(1)
    @Max(26)
    private int rowCount ;
    @Min(1)
    @Max(30)
    private int seatsPerRow;
}
