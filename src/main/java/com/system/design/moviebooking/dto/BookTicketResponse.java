package com.system.design.moviebooking.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookTicketResponse {
    private Long BookingId;
    private ResponseStatus status;
    private String message;
}
