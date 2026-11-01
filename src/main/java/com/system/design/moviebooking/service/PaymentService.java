package com.system.design.moviebooking.service;

import com.system.design.moviebooking.dto.PaymentResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentService implements IPaymentService{
    @Override
    public PaymentResult pay(Long bookingId, BigDecimal amount) {
        return new PaymentResult(true, "MOCK-" + UUID.randomUUID());
    }
}
