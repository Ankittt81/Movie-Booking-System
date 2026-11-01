package com.system.design.moviebooking.service;

import com.system.design.moviebooking.dto.PaymentResult;

import java.math.BigDecimal;

public interface IPaymentService {
    PaymentResult pay(Long bookingId, BigDecimal amount);
}
