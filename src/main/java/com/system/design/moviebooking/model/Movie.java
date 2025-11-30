package com.system.design.moviebooking.model;


import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Movie extends BaseModel {
    private String title;
    private String director;
    private String year;
    private String genre;
    @ManyToMany
    private List<Artist> artists;
    @Enumerated
    @ElementCollection
    private List<Features> features;
    @Enumerated
    @ElementCollection
    private  List<Language>  languages;

}
