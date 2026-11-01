package com.system.design.moviebooking.repository;

import com.system.design.moviebooking.model.enums.BookingStatus;
import com.system.design.moviebooking.model.mainEntity.Booking;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Booking b where b.id = :id")
    Optional<Booking> findByIdForUpdate(@Param("id") Long id);

    @EntityGraph(attributePaths = {"show", "show.movie", "show.theatre"})
    Page<Booking> findByUserId(Long userId, Pageable pageable);

    @Modifying(flushAutomatically = true)
    @Query("update Booking b set b.status = :failed where b.status = :pending and b.expiresAt < :now")
    int failExpired(@Param("now") LocalDateTime now,
                    @Param("failed") BookingStatus failed,
                    @Param("pending") BookingStatus pending);
}
