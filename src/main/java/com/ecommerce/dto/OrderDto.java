package com.ecommerce.dto;

import com.ecommerce.entity.OrderStatus;
import com.ecommerce.entity.PaymentMode;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class OrderDto {
    private Long id;
    private Long userId;
    private Double totalAmount;
    private Instant orderDate;
    private OrderStatus orderStatus;
    private PaymentMode paymentMode;
    private List<OrderItemDto> items;
}
