package com.system.design.moviebooking.repository;

import com.system.design.moviebooking.model.mainEntity.Show;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {
    Page<Show> findByTheatreId(Long theatreId, Pageable pageable);
    @Query("""
            select s from Show s
            join fetch s.theatre t
            join fetch s.movie m
            where m.id = :movieId
              and s.startTime >= :from and s.startTime < :to
              and t.status = com.system.design.moviebooking.model.enums.TheatreStatus.APPROVED
            order by t.name, s.startTime
            """)
    List<Show> findShowsForMovie(@Param("movieId") Long movieId,
                                 @Param("from") LocalDateTime from,
                                 @Param("to") LocalDateTime to);
}
