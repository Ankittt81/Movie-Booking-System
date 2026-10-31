package com.system.design.moviebooking.dto;

import com.system.design.moviebooking.model.enums.SeatStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeatDto {
    private String label;
    private SeatStatus status;
}
