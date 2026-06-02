package com.minimarket.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateOrdemRequest {

    @NotNull
    private Long userId;

    @NotEmpty
    @Valid
    private List<CreateOrderItemRequest> items;
}
