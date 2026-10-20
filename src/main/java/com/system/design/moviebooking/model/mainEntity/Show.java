package com.system.design.moviebooking.model.mainEntity;

import com.system.design.moviebooking.model.futureScope.Screen;
import com.system.design.moviebooking.model.futureScope.ShowSeat;
import com.system.design.moviebooking.model.futureScope.ShowSeatType;
import com.system.design.moviebooking.model.enums.Features;
import com.system.design.moviebooking.model.enums.Language;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@Entity(name="shows")
public class Show extends BaseModel {
//    private String name;
//    private LocalDate date;
//    private LocalTime time;
//    private Integer ticketPrice;
//    private Integer totalSeats;
//    private List<Integer> bookedSeats=new ArrayList<>();
//    @ManyToOne
//    private Movie movie;
//    @ManyToOne
//    private Screen screen;
//    @ManyToOne
//    private Theatre theatre;
//    @OneToMany(mappedBy = "show")
//    private List<ShowSeat> showSeats;
//    @OneToMany(mappedBy = "show")
//    private List<ShowSeatType> showSeatTypes;
//    @Enumerated
//    private Language language;
//    @Enumerated
//    @ElementCollection
//    private List<Features>  features;
}

//show M: 1 Movie
