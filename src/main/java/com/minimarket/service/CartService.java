package com.minimarket.service;

import com.minimarket.dto.CartItemRequest;
import com.minimarket.dto.CartResponse;
import com.minimarket.dto.CheckoutResponse;
import com.minimarket.entity.Cart;
import com.minimarket.entity.CartItem;
import com.minimarket.entity.Order;
import com.minimarket.entity.OrderItem;
import com.minimarket.entity.User;
import com.minimarket.enums.CartStatus;
import com.minimarket.enums.OrderStatus;
import com.minimarket.entity.Product;
import com.minimarket.exception.BusinessException;
import com.minimarket.mapper.CartMapper;
import com.minimarket.repository.CartRepository;
import com.minimarket.repository.CartItemRepository;
import com.minimarket.repository.OrderRepository;
import com.minimarket.repository.ProductRepository;
import com.minimarket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    //Cria um carrinho novo para o usuário
    @Transactional
    public CartResponse createCart(Long userId) {
        if (cartRepository.existsByUserIdAndStatus(userId, CartStatus.OPEN)) {
            throw new BusinessException("User already has an open cart");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        Cart cart = new Cart();
        cart.setUser(user);

        return cartMapper.toResponse(cartRepository.save(cart));
    }

    //Busca o carrinho pelo id
    public CartResponse getCart(Long cartId) {
        Cart cart = findCartById(cartId);
        return cartMapper.toResponse(cart);
    }

    //Adiciona um produto ao carrinho e se o produto já estiver no carrinho, soma a quantidade
    @Transactional
    public CartResponse addItem(Long cartId, CartItemRequest request) {
        Cart cart = findOpenCart(cartId);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BusinessException("Product not found"));

        //Valida se tem estoque suficiente
        if (product.getStockQuantity() < request.getQuantity()) {
            throw new BusinessException("Insufficient stock");
        }

        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cartId, product.getId())
                .orElse(null);

        if (item == null) {
            //Produto novo no carrinho — cria item
            item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setUnitPrice(product.getPrice());
            item.setQuantity(request.getQuantity());
            item.recalculateSubtotal();
            cart.getItems().add(item);
        } else {
            //Produto já existe — só soma a quantidade
            int newQty = item.getQuantity() + request.getQuantity();
            if (product.getStockQuantity() < newQty) {
                throw new BusinessException("Insufficient stock for requested quantity");
            }
            item.setQuantity(newQty);
            item.recalculateSubtotal();
        }

        return cartMapper.toResponse(cartRepository.save(cart));
    }

    //Remove um item do carrinho pelo id do item
    @Transactional
    public CartResponse removeItem(Long cartId, Long itemId) {
        Cart cart = findOpenCart(cartId);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException("Item not found"));

        cart.getItems().remove(item);

        return cartMapper.toResponse(cartRepository.save(cart));
    }

    //Finaliza o carrinho e gera um pedido
    @Transactional
    public CheckoutResponse checkout(Long cartId) {
        Cart cart = findOpenCart(cartId);

        if (cart.getItems().isEmpty()) {
            throw new BusinessException("Cart is empty");
        }

        //Monta o pedido
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.CREATED);
        order.setTotalAmount(cart.getTotal());

        //Converte cada CartItem em OrderItem
        List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            orderItem.setSubtotal(cartItem.getSubtotal());
            return orderItem;
        }).toList();

        order.getItems().addAll(orderItems);
        orderRepository.save(order);

        //Fecha o carrinho
        cart.setStatus(CartStatus.CLOSED);
        cartRepository.save(cart);

        return new CheckoutResponse(order.getId(), "Order placed successfully!");
    }

    private Cart findCartById(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new BusinessException("Cart not found"));
    }

    private Cart findOpenCart(Long cartId) {
        Cart cart = findCartById(cartId);
        if (cart.getStatus() != CartStatus.OPEN) {
            throw new BusinessException("Cart is already closed");
        }
        return cart;
    }

    public List<CartResponse> findByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .stream()
                .map(cartMapper::toResponse)
                .toList();
    }
}