package com.minimarket.controller;

import com.minimarket.dto.CartItemRequest;
import com.minimarket.dto.CartResponse;
import com.minimarket.dto.CheckoutResponse;
import com.minimarket.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartResponse create(@RequestParam Long userId) {
        return cartService.createCart(userId);
    }

    @GetMapping("/{id}")
    public CartResponse findById(@PathVariable Long id) {
        return cartService.getCart(id);
    }

    @PostMapping("/{id}/items")
    public CartResponse addItem(@PathVariable Long id,
                                @RequestBody @Valid CartItemRequest request) {
        return cartService.addItem(id, request);
    }

    @DeleteMapping("/{id}/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@PathVariable Long id,
                           @PathVariable Long itemId) {
        cartService.removeItem(id, itemId);
    }

    @PostMapping("/{id}/checkout")
    public CheckoutResponse checkout(@PathVariable Long id) {
        return cartService.checkout(id);
    }

    @GetMapping("/user/{userId}")
    public List<CartResponse> findByUserId(@PathVariable Long userId) {
        return cartService.findByUserId(userId);
    }
}