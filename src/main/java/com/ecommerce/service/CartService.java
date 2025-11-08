package com.ecommerce.service;

import com.ecommerce.dto.CartDto;
import com.ecommerce.dto.CartItemDto;
import com.ecommerce.entity.*;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       UserRepository userRepository,
                       ProductRepository productRepository,
                       ModelMapper modelMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    private Cart getOrCreateCartForUser(User user) {
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart c = Cart.builder().user(user).build();
            return cartRepository.save(c);
        });
    }

    @Transactional
    public CartDto addToCart(Long userId, Long productId, int quantity) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be > 0");
        if (product.getStock() < quantity) throw new IllegalArgumentException("Not enough stock");

        Cart cart = getOrCreateCartForUser(user);

        var existingOpt = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());
        if (existingOpt.isPresent()) {
            CartItem item = existingOpt.get();
            int newQty = item.getQuantity() + quantity;
            if (product.getStock() < newQty) throw new IllegalArgumentException("Not enough stock for requested quantity");
            item.setQuantity(newQty);
            cartItemRepository.save(item);
        } else {
            CartItem item = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .build();
            cart.getItems().add(item);
            cartRepository.save(cart); // cascades to save item
        }
        return toCartDto(cart);
    }

    @Transactional
    public CartDto updateCartItem(Long userId, Long productId, int quantity) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Cart cart = getOrCreateCartForUser(user);
        var existingOpt = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());
        if (existingOpt.isEmpty()) throw new ResourceNotFoundException("Cart item not found");

        CartItem item = existingOpt.get();
        if (quantity <= 0) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            if (product.getStock() < quantity) throw new IllegalArgumentException("Not enough stock");
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
        return toCartDto(cart);
    }

    @Transactional
    public CartDto removeFromCart(Long userId, Long productId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Cart cart = getOrCreateCartForUser(user);
        var existingOpt = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
        if (existingOpt.isPresent()) {
            CartItem item = existingOpt.get();
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        }
        return toCartDto(cart);
    }

    public CartDto getCart(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Cart cart = getOrCreateCartForUser(user);
        return toCartDto(cart);
    }

    private CartDto toCartDto(Cart cart) {
        CartDto dto = new CartDto();
        dto.setCartId(cart.getId());
        dto.setUserId(cart.getUser().getId());
        dto.setItems(cart.getItems().stream().map(it -> {
            CartItemDto itemDto = new CartItemDto();
            itemDto.setProductId(it.getProduct().getId());
            itemDto.setProductName(it.getProduct().getName());
            itemDto.setUnitPrice(it.getProduct().getPrice());
            itemDto.setQuantity(it.getQuantity());
            itemDto.setSubtotal(it.getProduct().getPrice() * it.getQuantity());
            return itemDto;
        }).collect(Collectors.toList()));
        dto.setTotalPrice(cart.getTotalPrice());
        return dto;
    }
}
