package com.system.design.moviebooking.model.futureScope;

import com.system.design.moviebooking.model.enums.Features;
import com.system.design.moviebooking.model.mainEntity.BaseModel;
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
