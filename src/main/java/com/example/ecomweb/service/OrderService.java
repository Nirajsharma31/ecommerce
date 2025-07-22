package com.example.ecomweb.service;

import com.example.ecomweb.entity.*;
import com.example.ecomweb.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private ProductService productService;
    
    @Transactional
    public Order createOrder(User user, String shippingAddress, String paymentMethod) {
        List<CartItem> cartItems = cartService.getCartItems(user);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        
        // Validate stock availability
        for (CartItem item : cartItems) {
            if (!productService.isProductAvailable(item.getProduct().getId(), item.getQuantity())) {
                throw new RuntimeException("Product " + item.getProduct().getName() + " is out of stock");
            }
        }
        
        BigDecimal totalAmount = cartService.getCartTotal(user);
        Order order = new Order(user, totalAmount, shippingAddress);
        order.setPaymentMethod(paymentMethod);
        order = orderRepository.save(order);
        
        // Create order items and update stock
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem(order, cartItem.getProduct(), 
                                              cartItem.getQuantity(), cartItem.getProduct().getPrice());
            order.getOrderItems().add(orderItem);
            
            // Update product stock
            productService.updateStock(cartItem.getProduct().getId(), cartItem.getQuantity());
        }
        
        // Clear cart
        cartService.clearCart(user);
        
        return orderRepository.save(order);
    }
    
    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }
    
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }
    
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }
    
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent()) {
            throw new RuntimeException("Order not found");
        }
        
        Order order = orderOpt.get();
        Order.OrderStatus oldStatus = order.getStatus();
        order.setStatus(status);
        
        // If order is cancelled, restore stock
        if (status == Order.OrderStatus.CANCELLED && oldStatus != Order.OrderStatus.CANCELLED) {
            for (OrderItem item : order.getOrderItems()) {
                productService.restoreStock(item.getProduct().getId(), item.getQuantity());
            }
        }
        
        return orderRepository.save(order);
    }
    
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }
    
    public long getPendingOrdersCount() {
        return orderRepository.countPendingOrders();
    }
}