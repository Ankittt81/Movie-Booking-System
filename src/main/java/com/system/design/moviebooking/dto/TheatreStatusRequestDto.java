package com.system.design.moviebooking.dto;

import com.system.design.moviebooking.model.enums.TheatreStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TheatreStatusRequestDto {
    private TheatreStatus status;
}
