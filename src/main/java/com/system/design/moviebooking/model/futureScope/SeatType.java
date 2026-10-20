package com.system.design.moviebooking.model.futureScope;

import com.system.design.moviebooking.model.mainEntity.BaseModel;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SeatType extends BaseModel {
    private String name;
}
