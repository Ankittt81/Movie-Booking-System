package com.system.design.moviebooking.repository;

import com.system.design.moviebooking.model.ShowSeat;
import com.system.design.moviebooking.model.ShowSeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeat,Long> {
    List<ShowSeat> findAllByIdAndStatus(Iterable<Long> ids, ShowSeatStatus status);


}
//select * from showSeat where Id In () and status=