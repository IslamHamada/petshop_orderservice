package com.islamhamada.petshop.service;

import org.springframework.stereotype.Service;

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
}
