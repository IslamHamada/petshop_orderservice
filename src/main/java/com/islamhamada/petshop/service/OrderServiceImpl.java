package com.islamhamada.petshop.service;

import com.islamhamada.petshop.contracts.dto.ElaborateCartItemDTO;
import com.islamhamada.petshop.contracts.dto.ProductDTO;
import com.islamhamada.petshop.entity.Order;
import com.islamhamada.petshop.entity.OrderItem;
import com.islamhamada.petshop.exception.OrderServiceException;
import com.islamhamada.petshop.external.service.CartService;
import com.islamhamada.petshop.external.service.ProductService;
import com.islamhamada.petshop.contracts.dto.ElaborateOrderDTO;
import com.islamhamada.petshop.contracts.dto.ElaborateOrderItemDTO;
import com.islamhamada.petshop.model.OrderCartRequest;
import com.islamhamada.petshop.repository.OrderItemRepository;
import com.islamhamada.petshop.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Log4j2
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
        log.info("Ordering cart of user with id: " + user_id);
        List<ElaborateCartItemDTO> cart = cartService.getCartByUser(user_id).getBody();
        if(cart.isEmpty())
            throw new OrderServiceException("Can't issue an order with an empty cart", "CANNOT_BE_ISSUED", HttpStatus.CONFLICT);
        Order order = Order.builder()
                .userId(user_id)
                .time(Instant.now())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .country(request.getCountry())
                .city(request.getCity())
                .houseNumber(request.getHouseNumber())
                .postalCode(request.getPostalCode())
                .street(request.getStreet())
                .build();

        List<OrderItem> orderItems = new ArrayList<>();
        cart.forEach(
                cartItem -> {
                    OrderItem orderItem = OrderItem.builder()
                            .count(cartItem.getCart_item_count())
                            .productId(cartItem.getProduct_id())
                            .build();
                    orderItems.add(orderItem);
                }
        );
        List<ElaborateOrderItemDTO> elaborateOrderItems = new ArrayList<>();
        orderItems.forEach(orderItem -> {
                    ProductDTO product = productService.getProductById(orderItem.getProductId()).getBody();
                    if(product.getQuantity() < orderItem.getCount())
                        throw new OrderServiceException("Not enough " + product.getName() + " in stock", "CANNOT_BE_ISSUED", HttpStatus.CONFLICT);
                    ElaborateOrderItemDTO elaborateOrderItem = ElaborateOrderItemDTO.builder()
                            .price(product.getPrice())
                            .count(orderItem.getCount())
                            .product_name(product.getName())
                            .product_id(product.getId())
                            .build();
                    elaborateOrderItems.add(elaborateOrderItem);
                }
        );
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
                .price(price)
                .firstName(order.getFirstName())
                .lastName(order.getLastName())
                .phoneNumber(order.getPhoneNumber())
                .country(order.getCountry())
                .city(order.getCity())
                .street(order.getStreet())
                .houseNumber(order.getHouseNumber())
                .postalCode(order.getPostalCode())
                .build();
        cartService.emptyCartOfUser(user_id);
        log.info("User cart successfully ordered");
        return elaborateOrder;
    }

    @Override
    public List<ElaborateOrderDTO> getOrders(long user_id) {
        log.info("Getting orders of user with id: " + user_id);
        List<Order> orders = orderRepository.findByUserIdOrderByTimeDesc(user_id);
        List<ElaborateOrderDTO> elaborateOrders = new ArrayList<>();
        for(Order order : orders){
            List<ElaborateOrderItemDTO> elaborateOrderItems = new ArrayList<>();
            List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(order.getId());
            for(OrderItem orderItem : orderItems){
                ProductDTO product = productService.getProductById(orderItem.getProductId()).getBody();
                ElaborateOrderItemDTO elaborateOrderItem = ElaborateOrderItemDTO.builder()
                        .product_id(product.getId())
                        .product_name(product.getName())
                        .count(orderItem.getCount())
                        .price(product.getPrice())
                        .build();
                elaborateOrderItems.add(elaborateOrderItem);
            }
            ElaborateOrderDTO elaborateOrder = ElaborateOrderDTO.builder()
                    .elaborateOrderItems(elaborateOrderItems)
                    .time(order.getTime())
                    .price(order.getPrice())
                    .firstName(order.getFirstName())
                    .lastName(order.getLastName())
                    .phoneNumber(order.getPhoneNumber())
                    .country(order.getCountry())
                    .city(order.getCity())
                    .street(order.getStreet())
                    .houseNumber(order.getHouseNumber())
                    .postalCode(order.getPostalCode())
                    .build();
            elaborateOrders.add(elaborateOrder);
        }
        log.info("Orders of user successfully fetched");
        return elaborateOrders;
    }
}
