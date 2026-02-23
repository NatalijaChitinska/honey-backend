package com.honey.shop.service;

import com.honey.shop.domain.Cart;
import com.honey.shop.domain.CartItem;
import com.honey.shop.domain.Product;
import com.honey.shop.dto.request.AddToCartRequest;
import com.honey.shop.dto.request.UpdateCartItemRequest;
import com.honey.shop.dto.response.CartResponse;
import com.honey.shop.exception.BadRequestException;
import com.honey.shop.exception.ResourceNotFoundException;
import com.honey.shop.mapper.CartMapper;
import com.honey.shop.repository.CartRepository;
import com.honey.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static com.honey.shop.util.Constants.*;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    /**
     * Returns the cart for the given token, or creates a new cart when token is null/empty.
     */
    @Transactional
    public CartResponse getOrCreate(String token) {
        if (token == null || token.isBlank()) {
            return createNewCart();
        }
        Cart cart = cartRepository.findByToken(token.trim())
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_CART, token));
        return cartMapper.toResponse(cart);
    }

    /**
     * Returns the cart for the given token. Throws if not found.
     */
    @Transactional(readOnly = true)
    public CartResponse getByToken(String token) {
        Cart cart = cartRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_CART, token));
        return cartMapper.toResponse(cart);
    }

    @Transactional
    public CartResponse addItem(String token, AddToCartRequest request) {
        Cart cart = findCartByToken(token);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_PRODUCT, request.getProductId()));

        Optional<CartItem> existing = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        int newQuantity = existing
                .map(item -> item.getQuantity() + request.getQuantity())
                .orElse(request.getQuantity());

        if (product.getStock() < newQuantity) {
            throw new BadRequestException("Insufficient stock for product: " + product.getName());
        }

        if (existing.isPresent()) {
            existing.get().setQuantity(newQuantity);
        } else {
            CartItem item = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cart.getItems().add(item);
        }

        cart.setUpdatedAt(Instant.now());
        cart = cartRepository.save(cart);
        return cartMapper.toResponse(cart);
    }

    @Transactional
    public CartResponse updateItemQuantity(String token, Long itemId, UpdateCartItemRequest request) {
        Cart cart = findCartByToken(token);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_CART_ITEM, itemId));

        if (item.getProduct().getStock() < request.getQuantity()) {
            throw new BadRequestException("Insufficient stock for product: " + item.getProduct().getName());
        }

        item.setQuantity(request.getQuantity());
        cart.setUpdatedAt(Instant.now());
        cart = cartRepository.save(cart);
        return cartMapper.toResponse(cart);
    }

    @Transactional
    public CartResponse removeItem(String token, Long itemId) {
        Cart cart = findCartByToken(token);
        boolean removed = cart.getItems().removeIf(item -> item.getId().equals(itemId));
        if (!removed) {
            throw new ResourceNotFoundException(RESOURCE_CART_ITEM, itemId);
        }
        cart.setUpdatedAt(Instant.now());
        cart = cartRepository.save(cart);
        return cartMapper.toResponse(cart);
    }

    @Transactional
    public CartResponse clear(String token) {
        Cart cart = findCartByToken(token);
        cart.getItems().clear();
        cart.setUpdatedAt(Instant.now());
        cart = cartRepository.save(cart);
        return cartMapper.toResponse(cart);
    }

    private Cart findCartByToken(String token) {
        if (token == null || token.isBlank()) {
            throw new BadRequestException("Cart token is required. Call GET /api/carts first to obtain a token.");
        }
        return cartRepository.findByToken(token.trim())
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_CART, token));
    }

    private CartResponse createNewCart() {
        Instant now = Instant.now();
        Cart cart = Cart.builder()
                .token(UUID.randomUUID().toString())
                .createdAt(now)
                .updatedAt(now)
                .items(new java.util.ArrayList<>())
                .build();
        cart = cartRepository.save(cart);
        return cartMapper.toResponse(cart);
    }
}
