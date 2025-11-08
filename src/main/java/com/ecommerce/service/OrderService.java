package com.ecommerce.service;

import com.ecommerce.dto.*;
import com.ecommerce.entity.*;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final Random random = new Random();

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        CartRepository cartRepository,
                        CartItemRepository cartItemRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository,
                        ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public OrderDto checkout(Long userId, CheckoutRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new IllegalArgumentException("Cart is empty"));

        // Validate cart items & stock
        for (CartItem item : cart.getItems()) {
            Product p = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + item.getProduct().getId()));
            if (p.getStock() < item.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + p.getName());
            }
        }

        // Calculate total
        double total = cart.getItems().stream()
                .mapToDouble(it -> it.getProduct().getPrice() * it.getQuantity())
                .sum();

        // Create order with PENDING (payment not done)
        Order order = Order.builder()
                .user(user)
                .totalAmount(total)
                .orderDate(Instant.now())
                .orderStatus(OrderStatus.PENDING)
                .paymentMode(request.getPaymentMode())
                .build();

        order = orderRepository.save(order);

        // persist order items
        for (CartItem ci : cart.getItems()) {
            OrderItem oi = OrderItem.builder()
                    .order(order)
                    .product(ci.getProduct())
                    .quantity(ci.getQuantity())
                    .price(ci.getProduct().getPrice()) // unit price snapshot
                    .build();
            order.getItems().add(oi);
        }
        order = orderRepository.save(order);

        // Simulate payment
        boolean paymentSuccess = simulatePayment(request.getSimulateSuccess());

        if (paymentSuccess) {
            // reduce inventory
            for (OrderItem oi : order.getItems()) {
                Product p = productRepository.findById(oi.getProduct().getId()).orElseThrow();
                int newStock = p.getStock() - oi.getQuantity();
                p.setStock(newStock);
                productRepository.save(p);
            }
            order.setOrderStatus(OrderStatus.PLACED);
            // clear cart
            for (CartItem ci : List.copyOf(cart.getItems())) {
                cart.getItems().remove(ci);
                cartItemRepository.delete(ci);
            }
            cartRepository.save(cart);
        } else {
            order.setOrderStatus(OrderStatus.FAILED);
        }
        order = orderRepository.save(order);

        return toOrderDto(order);
    }

    private boolean simulatePayment(Boolean simulateSuccess) {
        // If simulateSuccess provided, honor it. Otherwise random 80% success.
        if (simulateSuccess != null) return simulateSuccess;
        return random.nextDouble() < 0.85;
    }

    public List<OrderDto> getOrdersForUser(Long userId) {
        User u = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return orderRepository.findByUser(u).stream().map(this::toOrderDto).collect(Collectors.toList());
    }

    public OrderDto getOrder(Long id) {
        Order o = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return toOrderDto(o);
    }

    public List<OrderDto> listAllOrders() {
        return orderRepository.findAll().stream().map(this::toOrderDto).collect(Collectors.toList());
    }

    public OrderDto updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order o = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        o.setOrderStatus(newStatus);
        o = orderRepository.save(o);
        return toOrderDto(o);
    }

    private OrderDto toOrderDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setUserId(order.getUser().getId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderDate(order.getOrderDate());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setPaymentMode(order.getPaymentMode());
        dto.setItems(order.getItems().stream().map(oi -> {
            OrderItemDto itemDto = new OrderItemDto();
            itemDto.setProductId(oi.getProduct().getId());
            itemDto.setProductName(oi.getProduct().getName());
            itemDto.setQuantity(oi.getQuantity());
            itemDto.setPrice(oi.getPrice());
            return itemDto;
        }).collect(Collectors.toList()));
        return dto;
    }
}
