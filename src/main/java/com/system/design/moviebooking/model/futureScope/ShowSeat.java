package com.system.design.moviebooking.model.futureScope;


import com.system.design.moviebooking.model.enums.ShowSeatStatus;
import com.system.design.moviebooking.model.mainEntity.BaseModel;
import com.system.design.moviebooking.model.mainEntity.Show;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ShowSeat extends BaseModel {
    @ManyToOne
    private Show show;
    @ManyToOne
    private Seat seat;
    private ShowSeatStatus status;
}

// ShowSeat M:1 Show
// 1 A1 A
// 1 A2 A
// 1 A3 B
// 2 A1 B
// 2 A2 B