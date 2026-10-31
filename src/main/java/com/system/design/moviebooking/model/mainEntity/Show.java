package com.system.design.moviebooking.model.mainEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name="shows")
public class Show extends BaseModel {
    @ManyToOne
    private Movie movie;
    @ManyToOne
    private Theatre theatre;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal ticketPrice;
    private int rowCount;
    private int seatsPerRow;
//    private String name;
//    private LocalDate date;
//    private LocalTime time;
//    private Integer ticketPrice;
//    private Integer totalSeats;
//    private List<Integer> bookedSeats=new ArrayList<>();

//    @ManyToOne
//    private Screen screen;

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
