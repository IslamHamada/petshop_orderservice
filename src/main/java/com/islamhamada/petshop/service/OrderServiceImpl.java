package com.islamhamada.petshop.service;

import com.islamhamada.petshop.contracts.CartItemDTO;
import com.islamhamada.petshop.contracts.ProductDTO;
import com.islamhamada.petshop.entity.Order;
import com.islamhamada.petshop.entity.OrderItem;
import com.islamhamada.petshop.external.service.CartService;
import com.islamhamada.petshop.external.service.ProductService;
import com.islamhamada.petshop.model.ElaborateOrder;
import com.islamhamada.petshop.model.ElaborateOrderItem;
import com.islamhamada.petshop.repository.OrderItemRepository;
import com.islamhamada.petshop.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Override
    public ElaborateOrder orderUserCart(long user_id) {
        List<CartItemDTO> cart = cartService.getCartByUser(user_id).getBody();
        Order order = Order.builder()
                .userId(user_id)
                .time(Instant.now())
                .build();
        orderRepository.save(order);
        List<OrderItem> orderItems = new ArrayList<>();
        cart.forEach(
                cartItem -> {
                    OrderItem orderItem = OrderItem.builder()
                            .orderId(order.getId())
                            .count(cartItem.getCount())
                            .productId(cartItem.getProductId())
                            .build();
                    orderItemRepository.save(orderItem);
                    orderItems.add(orderItem);
                }
        );
        List<ElaborateOrderItem> elaborateOrderItems = new ArrayList<>();
        orderItems.forEach(orderItem -> {
                    ProductDTO product = productService.getProductById(orderItem.getProductId()).getBody();
                    ElaborateOrderItem elaborateOrderItem = ElaborateOrderItem.builder()
                            .price(product.getPrice())
                            .count(orderItem.getCount())
                            .product_name(product.getName())
                            .build();
                    elaborateOrderItems.add(elaborateOrderItem);
                }
        );
        ElaborateOrder elaborateOrder = ElaborateOrder.builder()
                .time(order.getTime())
                .elaborateOrderItems(elaborateOrderItems)
                .build();
        cartService.emptyCartOfUser(user_id);
        return elaborateOrder;
    }

    @Override
    public List<ElaborateOrder> getOrders(long user_id) {
        List<Order> orders = orderRepository.findOrderByUserId(user_id);
        List<ElaborateOrder> elaborateOrders = new ArrayList<>();
        for(Order order : orders){
            List<ElaborateOrderItem> elaborateOrderItems = new ArrayList<>();
            List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(order.getId());
            for(OrderItem orderItem : orderItems){
                ProductDTO product = productService.getProductById(orderItem.getProductId()).getBody();
                ElaborateOrderItem elaborateOrderItem = ElaborateOrderItem.builder()
                        .product_name(product.getName())
                        .count(orderItem.getCount())
                        .price(product.getPrice())
                        .build();
                elaborateOrderItems.add(elaborateOrderItem);
            }
            ElaborateOrder elaborateOrder = ElaborateOrder.builder()
                    .elaborateOrderItems(elaborateOrderItems)
                    .time(order.getTime())
                    .build();
            elaborateOrders.add(elaborateOrder);
        }
        return elaborateOrders;
    }
}
