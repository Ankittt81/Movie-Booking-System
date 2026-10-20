//package com.system.design.moviebooking.controller;
//
//import com.system.design.moviebooking.dto.BookTicketRequest;
//import com.system.design.moviebooking.dto.BookTicketResponse;
//import com.system.design.moviebooking.dto.ResponseStatus;
//import com.system.design.moviebooking.model.mainEntity.Booking;
//import com.system.design.moviebooking.service.BookingServiceImpl;
//import org.springframework.stereotype.Controller;
//
//@Controller
//public class BookingController {
//    private BookingServiceImpl bookingServiceImpl;
//
//    public BookingController(BookingServiceImpl bookingServiceImpl) {
//        this.bookingServiceImpl = bookingServiceImpl;
//    }
//
//    public BookTicketResponse bookTicket(BookTicketRequest request){
//        BookTicketResponse response = new BookTicketResponse();
//        try{
//           Booking booking= bookingServiceImpl.bookTicket(
//                    request.getShowId(),
//                    request.getUserId(),
//                    request.getShowSeatIds()
//            );
//           response.setBookingId(booking.getId());
//           response.setStatus(ResponseStatus.SUCCESS);
//           response.setMessage("Booking Confirmed.Please make the payment");
//        }catch(Exception e){
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setMessage(e.getMessage());
//        }
//        return response;
//    }
//}
