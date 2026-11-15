package com.system.design.moviebooking.dto.movieAndTheatre;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    @Positive
    private Integer duration;
    @NotBlank
    private String language;
    @NotBlank
    private String genre;
    @NotNull
    private LocalDate releaseDate;
    private String poster;
}
