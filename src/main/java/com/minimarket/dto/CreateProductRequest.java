package com.minimarket.dto;

import com.minimarket.enums.ProductCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateProductRequest {

    @NotBlank
    @Size(min = 2, max = 120)
    private String name;

    @Size(max = 255)
    private String description;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal price;

    @NotNull
    private ProductCategory category;

    @NotNull
    @Min(0)
    private Integer stockQuantity;
}
