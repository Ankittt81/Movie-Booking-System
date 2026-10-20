package com.system.design.moviebooking.model.mainEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Booking extends BaseModel {
//    private List<Integer> seats=new ArrayList<>();
//    private String transactionId;
//    @ManyToOne(fetch = FetchType.EAGER)
//    private Show bookedShow;
//    @ManyToOne
//    private User user;
//    private Date bookingDate;
//    private int noOfSeats;
//    @ManyToOne
//    private User bookedBy;
//    private int totalAmount;
//    @ManyToMany
//
//    private List<ShowSeat> bookedSeats;
//    @OneToMany(mappedBy = "booking")
//    private List<Payment>  payments;
//    @Enumerated(EnumType.STRING)
//    private BookingStatus status;
}

// Cancellation : One ShowSeat can be present in multiple bookings