package com.system.design.moviebooking.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RawFilters {
    private String title;
    private String genre;
    private String language;
    private String city;
    private Double maxPrice;
    private String day;
    private String timeOfDay;
    private String maxDuration;
}
