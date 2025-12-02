package com.system.design.moviebooking.controller;

import com.system.design.moviebooking.dto.BookTicketRequest;
import com.system.design.moviebooking.dto.BookTicketResponse;
import com.system.design.moviebooking.dto.ResponseStatus;
import com.system.design.moviebooking.model.Booking;
import com.system.design.moviebooking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class BookingController {
    private BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }
    public BookTicketResponse bookTicket(BookTicketRequest request){
        BookTicketResponse response = new BookTicketResponse();
        try{
           Booking booking= bookingService.bookTicket(
                    request.getShowId(),
                    request.getUserId(),
                    request.getShowSeatIds()
            );
           response.setBookingId(booking.getId());
           response.setStatus(ResponseStatus.SUCCESS);
           response.setMessage("Booking Confirmed.Please make the payment");
        }catch(Exception e){
            response.setStatus(ResponseStatus.FAILURE);
            response.setMessage(e.getMessage());
        }
        return response;
    }
}
