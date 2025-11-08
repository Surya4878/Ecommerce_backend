package com.ecommerce.controller;

import com.ecommerce.dto.CheckoutRequest;
import com.ecommerce.dto.OrderDto;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.security.UserDetailsImpl;
import com.ecommerce.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    private Long currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var principal = auth.getPrincipal();
        return ((UserDetailsImpl) principal).getId();
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderDto> checkout(@Valid @RequestBody CheckoutRequest request) {
        Long userId = currentUserId();
        return ResponseEntity.ok(orderService.checkout(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> myOrders() {
        Long userId = currentUserId();
        return ResponseEntity.ok(orderService.getOrdersForUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Long id) {
        Long userId = currentUserId();
        OrderDto order = orderService.getOrder(id);
        // allow owner or admin
        var auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin || order.getUserId().equals(userId)) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.status(403).build();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDto> updateStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<OrderDto>> listAllOrders() {
        return ResponseEntity.ok(orderService.listAllOrders());
    }
}
