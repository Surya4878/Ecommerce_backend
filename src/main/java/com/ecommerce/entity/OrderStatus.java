package com.ecommerce.entity;

public enum OrderStatus {
    PENDING,    // created but payment not done
    PLACED,     // payment success
    FAILED,     // payment failed
    SHIPPED,
    DELIVERED,
    CANCELLED
}
