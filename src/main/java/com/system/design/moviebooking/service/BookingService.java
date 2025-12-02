package com.system.design.moviebooking.service;

import com.system.design.moviebooking.model.*;
import com.system.design.moviebooking.repository.BookingRepository;
import com.system.design.moviebooking.repository.ShowRepository;
import com.system.design.moviebooking.repository.ShowSeatRepository;
import com.system.design.moviebooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    private ShowSeatRepository showSeatRepository;
    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private ShowRepository showRepository;

    @Autowired
    public BookingService(
            ShowRepository showRepository,
            UserRepository userRepository,
            ShowSeatRepository showSeatRepository,
            BookingRepository bookingRepository
    ){
        this.showSeatRepository = showSeatRepository;
        this.userRepository = userRepository;
        this.showRepository = showRepository;
        this.bookingRepository = bookingRepository;
    }

    public Booking bookTicket(
            Long showId,
            Long userId,
            List<Long> showSeatIds
    ){
        //1. get the user from the db
        Optional<User> userOptional= userRepository.findById(userId);
        User user=null;
        if(userOptional.isEmpty()){
            user=new  User();
            user.setUsername("USER" + userId);
            user=userRepository.save(user);
        }else{
            user=userOptional.get();
        }
        //2. get the show from the db
        Optional<Show> showOptional= showRepository.findById(showId);
        Show show=null;
        if(showOptional.isEmpty()){
            //create the exception explicitly instead of using Runtime
            throw new RuntimeException("Show not found");
        }
        show=showOptional.get();
        //3. get the seats from the db
        //select * from show_seats where id IN(_,_);
        //showSeatRepository.findAllById(showSeatIds);

        //select * from show_seats where id IN(_,_) and status
        List<ShowSeat> showSeats=showSeatRepository.findAllByIdAndStatus(showSeatIds, ShowSeatStatus.AVALIABLE);

        //4. check if all the seats are available
        if(showSeats.size()<showSeatIds.size()){
            throw new RuntimeException("Certain seats are already booked");
        }
        //5. If yes, block the seats
        for(ShowSeat showSeat:showSeats){
            showSeat.setStatus(ShowSeatStatus.BLOCKED);
        }
        showSeatRepository.saveAll(showSeats);

        //6. create the booking
        Booking booking=new Booking();
         booking.setBookedBy(user);
         booking.setBookingDate(new Date());
         booking.setBookedSeats(showSeats);
         booking.setStatus(BookingStatus.PENDING);

         // get the seat types
        //Implement logic to get totalprice
         booking.setTotalAmount(100);
         booking.setNoOfSeats(showSeats.size());
        return bookingRepository.save(booking);

    }
}
