package com.minimarket.controller;

import com.minimarket.dto.CreateOrdemRequest;
import com.minimarket.dto.OrderResponse;
import com.minimarket.dto.UpdateOrderStatusRequest;
import com.minimarket.service.OrderService;
import jakarta.validation.Valid;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@RequestBody @Valid CreateOrdemRequest request) {
        return orderService.create(request);
    }

    @PatchMapping("/{id}/status")
    public OrderResponse updateStatus(@PathVariable Long id,
                                      @RequestBody @Valid UpdateOrderStatusRequest request) {
        return orderService.updateStatus(id, request);
    }

    @GetMapping
    public Page<OrderResponse> findAll(Pageable pageable) {
        return orderService.findAll(pageable);
    }

    @GetMapping("/user/{userId}")
    public Page<OrderResponse> findByUserId(@PathVariable Long userId, Pageable pageable) {
        return orderService.findByUserId(userId, pageable);
    }

}
