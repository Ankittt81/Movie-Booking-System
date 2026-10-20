package com.system.design.moviebooking.model.futureScope;


import com.system.design.moviebooking.model.enums.PaymentMode;
import com.system.design.moviebooking.model.enums.PaymentStatus;
import com.system.design.moviebooking.model.mainEntity.BaseModel;
import com.system.design.moviebooking.model.mainEntity.Booking;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class Payment extends BaseModel {
    private Date date;
    private int amount;
    private String refNumber;
    @Enumerated
    private PaymentStatus paymentStatus;
    @Enumerated
    private PaymentMode paymentMode;
    @ManyToOne
    private Booking booking;
}
