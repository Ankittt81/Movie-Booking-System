package com.system.design.moviebooking.search;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class SearchFilters {
   private String title;
   private String genre;
   private String language;
   private String city;
    private BigDecimal maxPrice;
    private LocalDate date;
    private TimeOfDay TimeOfDay;
    private Integer maxDuration;

    @JsonIgnore
    public boolean isEmpty(){
        if(title!=null) return false;
        if(genre!=null) return false;
        if(language!=null) return false;
        if(city!=null) return false;
        if(maxPrice!=null) return false;
        if(date!=null) return false;
        if(TimeOfDay!=null) return false;
        if(maxDuration!=null) return false;
        return true;
    }
}
