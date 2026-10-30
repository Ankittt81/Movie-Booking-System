package com.system.design.moviebooking.dto;

import com.system.design.moviebooking.model.enums.TheatreStatus;

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
