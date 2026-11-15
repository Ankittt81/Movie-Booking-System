package com.system.design.moviebooking.repository;

import com.system.design.moviebooking.model.mainEntity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    Page<Movie> findByActiveTrueAndTitleContainingIgnoreCase(String title, Pageable pageable);
    Optional<Movie> findByIdAndActiveTrue(Long id);
}
