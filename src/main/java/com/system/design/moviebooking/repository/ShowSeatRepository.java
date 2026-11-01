package com.system.design.moviebooking.repository;

import com.system.design.moviebooking.model.enums.SeatStatus;
import com.system.design.moviebooking.model.mainEntity.Booking;
import com.system.design.moviebooking.model.mainEntity.ShowSeat;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeat,Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000"))
    @Query("""
        select s from ShowSeat s
         where s.show.id = :showId and s.seatLabel in :labels
         order by s.id
        """)
    List<ShowSeat> findSeatsForUpdate(@Param("showId") Long showId,
                                      @Param("labels") Collection<String> labels);

    @Modifying(flushAutomatically = true)
    @Query("""
        update ShowSeat s set s.status = :booked, s.heldUntil = null
         where s.booking.id = :bookingId and s.status = :held
        """)
    int markBooked(@Param("bookingId") Long bookingId,
                   @Param("booked") SeatStatus booked,
                   @Param("held") SeatStatus held);

    @Modifying(flushAutomatically = true)
    @Query("""
        update ShowSeat s set s.status = :available, s.heldUntil = null, s.booking = null
         where s.booking.id = :bookingId
        """)
    int releaseByBooking(@Param("bookingId") Long bookingId,
                         @Param("available") SeatStatus available);

    @Modifying(flushAutomatically = true)
    @Query("""
        update ShowSeat s set s.status = :available, s.heldUntil = null, s.booking = null
         where s.status = :held and s.heldUntil < :now
        """)
    int releaseExpired(@Param("now") LocalDateTime now,
                       @Param("available") SeatStatus available,
                       @Param("held") SeatStatus held);

    long countByBookingId(Long bookingId);

    List<ShowSeat> findByBookingIdOrderById(Long bookingId);
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