package com.system.design.moviebooking.search;

import com.system.design.moviebooking.model.enums.TheatreStatus;
import com.system.design.moviebooking.model.mainEntity.Movie;
import com.system.design.moviebooking.model.mainEntity.Show;
import com.system.design.moviebooking.model.mainEntity.Theatre;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class ShowSpecifications {

    private ShowSpecifications() {}

    public static Specification<Show> matching(SearchFilters f, LocalDateTime now) {
        return (root, query, cb) -> {
            Join<Show, Movie> movie = root.join("movie");
            Join<Show, Theatre> theatre = root.join("theatre");
            List<Predicate> p = new ArrayList<>();

            // always: approved theatre, active movie, show not started
            p.add(cb.equal(theatre.get("status"), TheatreStatus.ACTIVE));
            p.add(cb.isTrue(movie.get("active")));
            p.add(cb.greaterThan(root.<LocalDateTime>get("startTime"), now));

            if (f.getTitle() != null) {
                p.add(contains(cb, movie.<String>get("title"), f.getTitle()));
            }
            if (f.getGenre() != null) {
                p.add(cb.equal(cb.lower(movie.<String>get("genre")), f.getGenre().toLowerCase()));
            }
            if (f.getLanguage() != null) {
                p.add(cb.equal(cb.lower(movie.<String>get("language")), f.getLanguage().toLowerCase()));
            }
            if (f.getCity() != null) {
                p.add(contains(cb, theatre.<String>get("city"), f.getCity()));
            }
            if (f.getMaxPrice() != null) {
                p.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("ticketPrice"), f.getMaxPrice()));
            }
            if (f.getDate() != null) {
                p.add(cb.greaterThanOrEqualTo(root.<LocalDateTime>get("startTime"), f.getDate().atStartOfDay()));
                p.add(cb.lessThan(root.<LocalDateTime>get("startTime"), f.getDate().plusDays(1).atStartOfDay()));
            }
            if(f.getMaxDuration()!=null){
                p.add(cb.lessThanOrEqualTo(movie.get("duration"), f.getMaxDuration()));
            }
            return cb.and(p.toArray(new Predicate[0]));
        };
    }

    // case-insensitive "contains"; escapes % and _ so user text cannot act as wildcards
    private static Predicate contains(CriteriaBuilder cb, Expression<String> path, String value) {
        String escaped = value.toLowerCase()
                .replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
        return cb.like(cb.lower(path), "%" + escaped + "%", '\\');
    }
}
