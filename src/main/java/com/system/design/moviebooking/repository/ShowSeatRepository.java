package com.system.design.moviebooking.repository;

import com.system.design.moviebooking.model.enums.SeatStatus;
import com.system.design.moviebooking.model.mainEntity.ShowSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeat,Long> {
    List<ShowSeat> findByShowIdOrderBySeatLabel(Long showId);
    List<ShowSeat> findByShowIdOrderById(Long showId);

    // simple version, ignores expired holds
    long countByShowIdAndStatus(Long showId, SeatStatus status);

    // use this one: an expired hold counts as available
    @Query("""
            select count(s) from ShowSeat s
            where s.show.id = :showId
              and (s.status = com.system.design.moviebooking.model.enums.SeatStatus.AVAILABLE
                   or (s.status = com.system.design.moviebooking.model.enums.SeatStatus.HELD
                       and s.heldUntil < :now))
            """)
    long countAvailable(@Param("showId") Long showId, @Param("now") LocalDateTime now);

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    List<ShowSeat> findAllByIdAndStatus(Iterable<Long> ids, ShowSeatStatus status);


}
//select * from showSeat where Id In () and status=