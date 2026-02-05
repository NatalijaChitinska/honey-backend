package com.honey.shop.controller;

import com.honey.shop.dto.request.AddToCartRequest;
import com.honey.shop.dto.request.UpdateCartItemRequest;
import com.honey.shop.dto.response.ApiResponse;
import com.honey.shop.dto.response.CartResponse;
import com.honey.shop.service.CartService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@Validated
public class CartController {

    private static final String CART_TOKEN_HEADER = "X-Cart-Token";

    private final CartService cartService;

    /**
     * Get current cart. If no X-Cart-Token header is sent, a new cart is created and returned.
     * Client should store the token from the response for subsequent requests.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getOrCreateCart(
            @RequestHeader(value = CART_TOKEN_HEADER, required = false) String cartToken) {
        CartResponse cart = cartService.getOrCreate(cartToken);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    /**
     * Add a product to the cart. X-Cart-Token header is required.
     */
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(
            @RequestHeader(CART_TOKEN_HEADER) String cartToken,
            @Valid @RequestBody AddToCartRequest request) {
        CartResponse cart = cartService.addItem(cartToken, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(cart, "Item added to cart"));
    }

    /**
     * Update the quantity of a cart item. X-Cart-Token header is required.
     */
    @PatchMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateItem(
            @RequestHeader(CART_TOKEN_HEADER) String cartToken,
            @PathVariable @Positive(message = "Cart item ID must be positive") Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        CartResponse cart = cartService.updateItemQuantity(cartToken, itemId, request);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    /**
     * Remove an item from the cart. X-Cart-Token header is required.
     */
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            @RequestHeader(CART_TOKEN_HEADER) String cartToken,
            @PathVariable @Positive(message = "Cart item ID must be positive") Long itemId) {
        CartResponse cart = cartService.removeItem(cartToken, itemId);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    /**
     * Clear all items from the cart. X-Cart-Token header is required.
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<CartResponse>> clearCart(
            @RequestHeader(CART_TOKEN_HEADER) String cartToken) {
        CartResponse cart = cartService.clear(cartToken);
        return ResponseEntity.ok(ApiResponse.success(cart, "Cart cleared"));
    }
}
