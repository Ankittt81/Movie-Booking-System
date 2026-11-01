package com.system.design.moviebooking.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HoldSeatsRequestDto {
    private Long showId;
    List<String> seatLabels;
}
