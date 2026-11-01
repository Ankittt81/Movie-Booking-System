package com.system.design.moviebooking.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentResult {
   private  boolean success;
   private String reference;

   public PaymentResult(boolean success, String reference) {
       this.success = success;
       this.reference = reference;
   }
}
