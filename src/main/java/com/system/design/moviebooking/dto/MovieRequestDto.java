package com.system.design.moviebooking.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MovieRequestDto {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotNull
    private Integer duration;
    @NotBlank
    private String language;
    @NotBlank
    private String genre;
    @NotNull
    private LocalDate releaseDate;
    private String poster;
}
