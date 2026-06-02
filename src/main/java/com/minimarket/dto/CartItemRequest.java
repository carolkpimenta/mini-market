package com.minimarket.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemRequest {
    @NotNull(message = "Inform the product:")
    Long productId;

    @NotNull(message = "Inform the quantity")
    @Min(value = 1, message = "Minimum quantity is 1")
    Integer quantity;

}

