package com.minimarket.dto;

import com.minimarket.enums.CartStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class CartResponse {
    private Long id;
    private Long userId;
    private CartStatus status;
    private LocalDateTime createdAt;
    private List<CartItemResponse> items;
    private BigDecimal total;
}
