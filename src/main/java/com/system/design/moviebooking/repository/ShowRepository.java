package com.system.design.moviebooking.repository;

import com.system.design.moviebooking.model.mainEntity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {
}
