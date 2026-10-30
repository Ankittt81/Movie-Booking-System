package com.system.design.moviebooking.repository;

import com.system.design.moviebooking.model.enums.TheatreStatus;
import com.system.design.moviebooking.model.mainEntity.Theatre;
import com.system.design.moviebooking.model.mainEntity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheatreRepository extends JpaRepository<Theatre,Long> {
    Page<Theatre> findByOwnerId(Long ownerId, Pageable pageable);
    Page<Theatre> findByStatus(TheatreStatus status, Pageable pageable);
}
