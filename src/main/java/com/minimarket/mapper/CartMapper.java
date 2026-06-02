package com.minimarket.mapper;

import com.minimarket.dto.CartItemResponse;
import com.minimarket.dto.CartResponse;
import com.minimarket.entity.Cart;
import com.minimarket.entity.CartItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartMapper {
    public CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(this::toItemResponse)
                .toList();
return new CartResponse(
        cart.getId(),
        cart.getUser().getId(),
        cart.getStatus(),
        cart.getCreatedAt(),
        items,
        cart.getTotal()
);
    }

    public CartItemResponse toItemResponse(CartItem item){
        return new CartItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal()
        );
    }
}
