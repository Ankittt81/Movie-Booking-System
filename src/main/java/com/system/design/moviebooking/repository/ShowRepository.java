package com.system.design.moviebooking.repository;

import com.system.design.moviebooking.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowRepository extends JpaRepository<Show, Long> {
}
