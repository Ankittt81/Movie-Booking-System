package com.system.design.moviebooking.model.mainEntity;

import com.system.design.moviebooking.model.enums.TheatreStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
public class Theatre extends BaseModel {
    private String name;
    private String address;
    private String city;
    private String phone;
    private String email;
    @Enumerated(EnumType.STRING)
    private TheatreStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;
//
//    private User owner;
//    @OneToMany
//    @JoinColumn(name="theatre_id")
//    private List<Screen>  screens;
//    @ManyToOne
//    private Region region;
}
