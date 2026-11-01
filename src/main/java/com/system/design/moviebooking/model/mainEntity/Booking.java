package com.system.design.moviebooking.model.mainEntity;

import com.system.design.moviebooking.model.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "bookings")
public class Booking extends BaseModel {
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    private Show show;
    private BigDecimal totalAmount;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
    private LocalDateTime expiresAt;
    private String paymentRef;
    @OneToMany(mappedBy = "booking")
    private List<ShowSeat> seats=new ArrayList<>();

//    private List<Integer> seats=new ArrayList<>();
//    private String transactionId;
//    @ManyToOne(fetch = FetchType.EAGER)
//    private Show bookedShow;

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