package com.system.design.moviebooking.dto.movieAndTheatre;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TheatreRequestDto {
    @NotBlank
    private String name;
    @NotBlank
    private String address;
    @NotBlank
    private String city;
    @NotBlank
    private String phone;
    @NotBlank
    @Email
    private String email;
}