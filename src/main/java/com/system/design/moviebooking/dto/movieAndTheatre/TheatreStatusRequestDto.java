package com.system.design.moviebooking.dto.movieAndTheatre;

import com.system.design.moviebooking.model.enums.TheatreStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TheatreStatusRequestDto {
    private TheatreStatus status;
}
