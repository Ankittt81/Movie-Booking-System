package com.system.design.moviebooking.repository;

import com.system.design.moviebooking.model.mainEntity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {

}
