package com.system.design.moviebooking.model.mainEntity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Movie extends BaseModel {
    private String title;
    @Column(length = 1000)
    private String description;
    private Integer duration;
    private String language;
    private String genre;
    private LocalDate releaseDate;
    private String poster;
    private boolean active=true;
    /*
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

     */

}
