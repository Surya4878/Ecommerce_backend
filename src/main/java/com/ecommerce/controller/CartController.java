package com.ecommerce.controller;

import com.ecommerce.dto.CartDto;
import com.ecommerce.service.CartService;
import com.ecommerce.security.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    private Long currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var principal = auth.getPrincipal();
        return ((UserDetailsImpl) principal).getId();
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<CartDto> addToCart(@PathVariable Long productId,
                                             @RequestParam(defaultValue = "1") @Min(1) int quantity) {
        Long userId = currentUserId();
        return ResponseEntity.ok(cartService.addToCart(userId, productId, quantity));
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity<CartDto> updateCart(@PathVariable Long productId,
                                              @RequestParam @Min(0) int quantity) {
        Long userId = currentUserId();
        return ResponseEntity.ok(cartService.updateCartItem(userId, productId, quantity));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<CartDto> removeFromCart(@PathVariable Long productId) {
        Long userId = currentUserId();
        return ResponseEntity.ok(cartService.removeFromCart(userId, productId));
    }

    @GetMapping
    public ResponseEntity<CartDto> getCart() {
        Long userId = currentUserId();
        return ResponseEntity.ok(cartService.getCart(userId));
    }
}
