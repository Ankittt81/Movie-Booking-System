package com.system.design.moviebooking.model.mainEntity;


import com.system.design.moviebooking.model.enums.SeatStatus;
import com.system.design.moviebooking.model.enums.ShowSeatStatus;
import com.system.design.moviebooking.model.futureScope.Seat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"show_id", "seat_label"}))
public class ShowSeat extends BaseModel {
    @ManyToOne(fetch = FetchType.LAZY)
    private Show show;
    private String seatLabel;
    @Enumerated(EnumType.STRING)
    private SeatStatus status;
    private LocalDateTime heldUntil;
    @ManyToOne(fetch = FetchType.LAZY)
    private Booking booking;
//    @ManyToOne
//    private Seat seat;
//    private ShowSeatStatus status;
}

// ShowSeat M:1 Show
// 1 A1 A
// 1 A2 A
// 1 A3 B
// 2 A1 B
// 2 A2 B