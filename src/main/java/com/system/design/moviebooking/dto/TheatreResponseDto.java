package com.system.design.moviebooking.dto;

import com.system.design.moviebooking.model.enums.TheatreStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TheatreResponseDto {
    private Long id;
    private String name;
    private String address;
    private String city;
    private String phone;
    private String email;
    private TheatreStatus status;
    private Long ownerId;
    private String ownerName;
}
