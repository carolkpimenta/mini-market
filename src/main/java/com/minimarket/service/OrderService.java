package com.minimarket.service;

import com.minimarket.dto.*;
import com.minimarket.entity.Order;
import com.minimarket.entity.OrderItem;
import com.minimarket.entity.Product;
import com.minimarket.entity.User;
import com.minimarket.enums.OrderStatus;
import com.minimarket.exception.BusinessException;
import com.minimarket.exception.ResourceNotFoundException;
import com.minimarket.repository.OrderRepository;
import com.minimarket.repository.ProductRepository;
import com.minimarket.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }
//usada quando muitas transações são feitas
    @Transactional
    public OrderResponse create(CreateOrdemRequest request) {
        //busca usuario do pedido
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));
//criando objeto
        Order order = new Order();
        order.setUser(user);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.CREATED);
//percorre pelos itens enviado
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CreateOrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemRequest.getProductId()));

            if (!product.getActive()) {
                throw new BusinessException("Product is inactive: " + product.getName());
            }

            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new BusinessException("Insufficient stock for product: " + product.getName());
            }
            //calcula o subtotal
            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            //cria a ordemItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setSubtotal(subtotal);
            //baixa o estoque
            product.setStockQuantity(product.getStockQuantity() - itemRequest.getQuantity());
            //calcular o total do pedidos
            orderItems.add(orderItem);
            totalAmount = totalAmount.add(subtotal);
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);
        //salva pedido
        Order savedOrder = orderRepository.save(order);

        List<OrderItemResponse> itemResponses = savedOrder.getItems()
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal()
                ))
                .toList();

        return new OrderResponse(
                savedOrder.getId(),
                savedOrder.getUser().getId(),
                savedOrder.getCreatedAt(),
                savedOrder.getStatus(),
                savedOrder.getTotalAmount(),
                itemResponses
        );
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        List<OrderItemResponse> itemResponses = order.getItems()
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal()
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getCreatedAt(),
                order.getStatus(),
                order.getTotalAmount(),
                itemResponses
        );
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findAll() {
        return orderRepository.findAll()
                .stream()
                .map(order -> {
                    List<OrderItemResponse> itemResponses = order.getItems()
                            .stream()
                            .map(item -> new OrderItemResponse(
                                    item.getProduct().getId(),
                                    item.getProduct().getName(),
                                    item.getQuantity(),
                                    item.getUnitPrice(),
                                    item.getSubtotal()
                            ))
                            .toList();

                    return new OrderResponse(
                            order.getId(),
                            order.getUser().getId(),
                            order.getCreatedAt(),
                            order.getStatus(),
                            order.getTotalAmount(),
                            itemResponses
                    );
                })
                .toList();
    }
    @Transactional(readOnly = true)
    public List<OrderResponse> findByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        return orderRepository.findByUserId(userId)
                .stream()
                .map(order -> {
                    List<OrderItemResponse> itemResponses = order.getItems()
                            .stream()
                            .map(item -> new OrderItemResponse(
                                    item.getProduct().getId(),
                                    item.getProduct().getName(),
                                    item.getQuantity(),
                                    item.getUnitPrice(),
                                    item.getSubtotal()
                            ))
                            .toList();

                    return new OrderResponse(
                            order.getId(),
                            order.getUser().getId(),
                            order.getCreatedAt(),
                            order.getStatus(),
                            order.getTotalAmount(),
                            itemResponses
                    );
                })
                .toList();
    }
    @Transactional
    public OrderResponse updateStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        order.setStatus(request.getStatus());

        Order updatedOrder = orderRepository.save(order);

        List<OrderItemResponse> itemResponses = updatedOrder.getItems()
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal()
                ))
                .toList();

        return new OrderResponse(
                updatedOrder.getId(),
                updatedOrder.getUser().getId(),
                updatedOrder.getCreatedAt(),
                updatedOrder.getStatus(),
                updatedOrder.getTotalAmount(),
                itemResponses
        );
    }
@Transactional(readOnly = true)
public Page<OrderResponse> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable).map(order -> {
            List<OrderItemResponse> itemResponses = order.getItems()
                    .stream()
                    .map(item -> new OrderItemResponse(
                            item.getProduct().getId(),
                            item.getProduct().getName(),
                            item.getQuantity(),
                            item.getUnitPrice(),
                            item.getSubtotal()
                    ))
                    .toList();
            return new OrderResponse(
                    order.getId(),
                    order.getUser().getId(),
                    order.getCreatedAt(),
                    order.getStatus(),
                    order.getTotalAmount(),
                    itemResponses
            );
        });
}

    @Transactional(readOnly = true)
    public Page<OrderResponse> findByUserId(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        return orderRepository.findByUserId(userId, pageable)
                .map(order -> {
                    List<OrderItemResponse> itemResponses = order.getItems()
                            .stream()
                            .map(item -> new OrderItemResponse(
                                    item.getProduct().getId(),
                                    item.getProduct().getName(),
                                    item.getQuantity(),
                                    item.getUnitPrice(),
                                    item.getSubtotal()
                            ))
                            .toList();

                    return new OrderResponse(
                            order.getId(),
                            order.getUser().getId(),
                            order.getCreatedAt(),
                            order.getStatus(),
                            order.getTotalAmount(),
                            itemResponses
                    );
                });
    }

}
