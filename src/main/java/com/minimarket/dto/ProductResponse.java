package com.minimarket.dto;

import com.minimarket.enums.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private ProductCategory category;
    private Integer stockQuantity;
    private boolean active;
}


