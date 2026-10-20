package com.system.design.moviebooking.model.mainEntity;

import com.system.design.moviebooking.model.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class User extends BaseModel {
    private String name;
    @Column(unique = true)
    private String email;
    private String password;
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<Role> role=new ArrayList<>();
//    @OneToMany(mappedBy = "bookedBy")
//    //if not have connection
//    //@JoinColumn(name="booked_by_id")
//    private List<Booking> bookings;
}
