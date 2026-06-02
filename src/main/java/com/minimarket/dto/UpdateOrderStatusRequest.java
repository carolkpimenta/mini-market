package com.minimarket.dto;

import com.minimarket.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateOrderStatusRequest {

    @NotNull(message = "Status is required")
    private OrderStatus status;

    public UpdateOrderStatusRequest() {

    }

    public OrderStatus getStatus() {
        return status;
    }

    public UpdateOrderStatusRequest(OrderStatus status) {
        this.status = status;
    }

}
