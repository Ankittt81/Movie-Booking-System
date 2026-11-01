package com.system.design.moviebooking.config;


import com.system.design.moviebooking.model.enums.BookingStatus;
import com.system.design.moviebooking.model.enums.SeatStatus;
import com.system.design.moviebooking.repository.BookingRepository;
import com.system.design.moviebooking.repository.ShowSeatRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class HoldExpiryJob {
    private final BookingRepository bookingRepository;
    private final ShowSeatRepository showSeatRepository;

    public HoldExpiryJob(BookingRepository bookingRepository, ShowSeatRepository showSeatRepository) {
        this.bookingRepository = bookingRepository;
        this.showSeatRepository = showSeatRepository;
    }

    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void releaseExpiredHolds() {
        LocalDateTime now = LocalDateTime.now();
        bookingRepository.failExpired(now, BookingStatus.FAILED, BookingStatus.PENDING);
        showSeatRepository.releaseExpired(now, SeatStatus.AVAILABLE, SeatStatus.HELD);
    }
}
