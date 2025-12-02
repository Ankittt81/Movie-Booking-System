package com.system.design.moviebooking.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@Entity
public class Screen  extends BaseModel {
    private String name;
    @Enumerated
    @ElementCollection
    private List<Features> screenFeatures;
    @OneToMany
     @JoinColumn(name="screen_id")
    private List<Seat> seats;

}
