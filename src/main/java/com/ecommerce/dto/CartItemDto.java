package com.ecommerce.dto;

import lombok.Data;

@Data
public class CartItemDto {
    private Long productId;
    private String productName;
    private Double unitPrice;
    private Integer quantity;
    private Double subtotal;
}
