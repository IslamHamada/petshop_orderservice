package com.islamhamada.petshop.service;

import com.islamhamada.petshop.contracts.dto.ElaborateCartItemDTO;
import com.islamhamada.petshop.contracts.dto.ProductDTO;
import com.islamhamada.petshop.entity.Order;
import com.islamhamada.petshop.entity.OrderItem;
import com.islamhamada.petshop.exception.OrderServiceException;
import com.islamhamada.petshop.external.service.CartService;
import com.islamhamada.petshop.external.service.ProductService;
import com.islamhamada.petshop.contracts.dto.ElaborateOrderDTO;
import com.islamhamada.petshop.model.ElaborateOrderItem;
import com.islamhamada.petshop.repository.OrderItemRepository;
import com.islamhamada.petshop.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ElaborateOrderDTO orderUserCart(long user_id, OrderCartRequest request) {
        List<ElaborateCartItemDTO> cart = cartService.getCartByUser(user_id).getBody();
        Order order = Order.builder()
                .userId(user_id)
                .time(Instant.now())
                .build();

        List<OrderItem> orderItems = new ArrayList<>();
        cart.forEach(
                cartItem -> {
                    OrderItem orderItem = OrderItem.builder()
                            .orderId(order.getId())
                            .count(cartItem.getCart_item_count())
                            .productId(cartItem.getProduct_id())
                            .build();
                    orderItems.add(orderItem);
                }
        );
        List<ElaborateOrderItem> elaborateOrderItems = new ArrayList<>();
        orderItems.forEach(orderItem -> {
                    ProductDTO product = productService.getProductById(orderItem.getProductId()).getBody();
                    if(product.getQuantity() < orderItem.getCount())
                        throw new OrderServiceException("Not enough " + product.getName() + " in stock", 409, HttpStatus.CONFLICT);
                    ElaborateOrderItem elaborateOrderItem = ElaborateOrderItem.builder()
                            .price(product.getPrice())
                            .count(orderItem.getCount())
                            .product_name(product.getName())
                            .build();
                    elaborateOrderItems.add(elaborateOrderItem);
                }
        );
        orderItems.forEach(o -> orderItemRepository.save(o));
        double price = elaborateOrderItems.stream()
                .mapToDouble(o -> o.getPrice() * o.getCount())
                .sum();
        order.setPrice(price);
        long order_id = orderRepository.save(order).getId();
        orderItems.forEach(o -> o.setOrderId(order_id));
        orderItems.forEach(o -> orderItemRepository.save(o));
        ElaborateOrderDTO elaborateOrder = ElaborateOrderDTO.builder()
                .time(order.getTime())
                .elaborateOrderItems(elaborateOrderItems)
                .price(order.getPrice())
                .build();
        cartService.emptyCartOfUser(user_id);
        return elaborateOrder;
    }

    @Override
    public List<ElaborateOrderDTO> getOrders(long user_id) {
        List<Order> orders = orderRepository.findByUserIdOrderByTimeDesc(user_id);
        List<ElaborateOrderDTO> elaborateOrders = new ArrayList<>();
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
            ElaborateOrderDTO elaborateOrder = ElaborateOrderDTO.builder()
                    .elaborateOrderItems(elaborateOrderItems)
                    .time(order.getTime())
                    .build();
            elaborateOrders.add(elaborateOrder);
        }
        return elaborateOrders;
    }
}
