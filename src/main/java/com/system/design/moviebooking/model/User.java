package com.system.design.moviebooking.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class User extends BaseModel{
    private String username;
    private String email;
    @OneToMany(mappedBy = "bookedBy")
    //if not have connection
    //@JoinColumn(name="booked_by_id")
    private List<Booking> bookings;
}
