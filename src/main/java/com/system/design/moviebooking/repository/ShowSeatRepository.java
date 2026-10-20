package com.system.design.moviebooking.repository;

import com.system.design.moviebooking.model.futureScope.ShowSeat;
import com.system.design.moviebooking.model.enums.ShowSeatStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeat,Long> {

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    List<ShowSeat> findAllByIdAndStatus(Iterable<Long> ids, ShowSeatStatus status);


}
//select * from showSeat where Id In () and status=