package com.ecommerce.dto;

import com.ecommerce.entity.PaymentMode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckoutRequest {
    @NotNull
    private PaymentMode paymentMode;

    // Optional: if provided, forces payment success / failure. If null, payment will be randomly simulated.
    private Boolean simulateSuccess;
}
