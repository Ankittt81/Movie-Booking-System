package com.system.design.moviebooking.dto.booking;


import com.system.design.moviebooking.model.enums.ResponseStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookTicketResponse {
    private Long BookingId;
    private ResponseStatus status;
    private String message;
}
