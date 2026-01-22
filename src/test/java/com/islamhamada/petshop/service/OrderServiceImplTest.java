package com.islamhamada.petshop.service;

import com.islamhamada.petshop.contracts.dto.ElaborateCartItemDTO;
import com.islamhamada.petshop.contracts.dto.ElaborateOrderDTO;
import com.islamhamada.petshop.contracts.dto.ElaborateOrderItemDTO;
import com.islamhamada.petshop.contracts.dto.ProductDTO;
import com.islamhamada.petshop.entity.Order;
import com.islamhamada.petshop.entity.OrderItem;
import com.islamhamada.petshop.exception.OrderServiceException;
import com.islamhamada.petshop.external.service.CartService;
import com.islamhamada.petshop.external.service.ProductService;
import com.islamhamada.petshop.model.OrderCartRequest;
import com.islamhamada.petshop.repository.OrderItemRepository;
import com.islamhamada.petshop.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CartService cartService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderService orderService = new OrderServiceImpl();

    @Nested
    @DisplayName("orderUserCart")
    class orderUserCart {

        @Test
        @DisplayName("success")
        void success() {
            int user_id = 1;
            OrderCartRequest orderCartRequest = getOrderCartRequestMock();

            List<ElaborateCartItemDTO> elaborateCartItemDTOList = getElaborateCartItemDTOList();
            List<ProductDTO> productDTOList = getProductDTOList();

            when(cartService.getCartByUser(user_id))
                    .thenReturn(new ResponseEntity<>(elaborateCartItemDTOList, HttpStatus.OK));

            List<OrderItem> orderItemList = new ArrayList<>();
            List<ElaborateOrderItemDTO> elaborateOrderItemDTOList = new ArrayList<>();
            double price = 0;
            for(int i = 0; i < productDTOList.size(); i++) {
                ProductDTO productDTO = productDTOList.get(i);
                ElaborateCartItemDTO elaborateCartItemDTO = elaborateCartItemDTOList.get(i);
                assertEquals(productDTO.getId(), elaborateCartItemDTO.getProduct_id());
                assertEquals(productDTO.getName(), elaborateCartItemDTO.getProduct_name());
                assertEquals(productDTO.getImage(), elaborateCartItemDTO.getProduct_image());
                assertEquals(productDTO.getPrice(), elaborateCartItemDTO.getProduct_price());
                when(productService.getProductById(productDTO.getId()))
                        .thenReturn(new ResponseEntity<>(productDTO, HttpStatus.OK));
                orderItemList.add(OrderItem.builder()
                        .count(elaborateCartItemDTO.getCart_item_count())
                        .productId(elaborateCartItemDTO.getProduct_id())
                        .build());
                elaborateOrderItemDTOList.add(ElaborateOrderItemDTO.builder()
                        .product_name(productDTO.getName())
                        .count(elaborateCartItemDTO.getCart_item_count())
                        .product_id(productDTO.getId())
                        .price(productDTO.getPrice())
                        .build());
                price += elaborateCartItemDTO.getProduct_price() * elaborateCartItemDTO.getCart_item_count();
            }

            when(orderRepository.save(any()))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            for(int i = 0; i < orderItemList.size(); i++) {
                OrderItem orderItem = orderItemList.get(i);
                when(orderItemRepository.save(any()))
                        .thenReturn(orderItem);
            }

            ElaborateOrderDTO elaborateOrderDTO = orderService.orderUserCart(user_id, orderCartRequest);

            verify(cartService, times(1))
                    .getCartByUser(user_id);
            for(ProductDTO productDTO : productDTOList){
                verify(productService, times(1))
                        .getProductById(productDTO.getId());
            }
            verify(orderRepository, times(1))
                    .save(any());

            verify(cartService, times(1))
                    .emptyCartOfUser(user_id);

            assertEquals(orderCartRequest.getFirstName(), elaborateOrderDTO.getFirstName());
            assertEquals(orderCartRequest.getLastName(), elaborateOrderDTO.getLastName());
            assertEquals(orderCartRequest.getPhoneNumber(), elaborateOrderDTO.getPhoneNumber());
            assertEquals(orderCartRequest.getCountry(), elaborateOrderDTO.getCountry());
            assertEquals(orderCartRequest.getCity(), elaborateOrderDTO.getCity());
            assertEquals(orderCartRequest.getPostalCode(), elaborateOrderDTO.getPostalCode());
            assertEquals(orderCartRequest.getStreet(), elaborateOrderDTO.getStreet());
            assertEquals(orderCartRequest.getHouseNumber(), elaborateOrderDTO.getHouseNumber());
            assertEquals(elaborateOrderItemDTOList.size(), elaborateOrderDTO.getElaborateOrderItems().size());
            for(int i = 0; i < elaborateOrderItemDTOList.size(); i++){
                ElaborateOrderItemDTO elaborateOrderItemDTO = elaborateOrderItemDTOList.get(i);
                ElaborateOrderItemDTO elaborateCartItemDTO2 = elaborateOrderDTO.getElaborateOrderItems().get(i);
                assertEquals(elaborateOrderItemDTO.getProduct_id(), elaborateCartItemDTO2.getProduct_id());
                assertEquals(elaborateOrderItemDTO.getPrice(), elaborateCartItemDTO2.getPrice());
                assertEquals(elaborateOrderItemDTO.getCount(), elaborateCartItemDTO2.getCount());
                assertEquals(elaborateOrderItemDTO.getProduct_name(), elaborateCartItemDTO2.getProduct_name());
            }
            assertEquals(price, elaborateOrderDTO.getPrice());
        }

        @Test
        @DisplayName("failure1")
        void failure1() {
            int user_id = 1;
            OrderCartRequest orderCartRequest = getOrderCartRequestMock();

            when(cartService.getCartByUser(anyLong()))
                    .thenReturn(new ResponseEntity<>(List.of(), HttpStatus.OK));

            OrderServiceException exception = assertThrows(OrderServiceException.class,
                    () -> orderService.orderUserCart(user_id, orderCartRequest));

            verify(cartService, times(1))
                    .getCartByUser(anyLong());

            assertEquals("Can't issue an order with an empty cart", exception.getMessage());
            assertEquals("ORDER_CANNOT_BE_ISSUED", exception.getError_code());
            assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
        }

        @Test
        @DisplayName("failure2")
        void failure2() {
            int user_id = 1;
            OrderCartRequest orderCartRequest = getOrderCartRequestMock();
            List<ElaborateCartItemDTO> elaborateCartItemDTOList = getElaborateCartItemDTOList();
            List<ProductDTO> productDTOList = getProductDTOList();
            elaborateCartItemDTOList.get(elaborateCartItemDTOList.size() - 1).setCart_item_count(productDTOList.get(productDTOList.size() - 1).getQuantity() + 1);

            when(cartService.getCartByUser(user_id))
                    .thenReturn(new ResponseEntity<>(elaborateCartItemDTOList, HttpStatus.OK));

            ProductDTO problematic_product = null;
            for(int i = 0; i < productDTOList.size(); i++) {
                ProductDTO productDTO = productDTOList.get(i);
                ElaborateCartItemDTO elaborateCartItemDTO = elaborateCartItemDTOList.get(i);
                if(productDTO.getQuantity() < elaborateCartItemDTO.getCart_item_count())
                    problematic_product = productDTO;
                assertEquals(productDTO.getId(), elaborateCartItemDTO.getProduct_id());
                assertEquals(productDTO.getName(), elaborateCartItemDTO.getProduct_name());
                assertEquals(productDTO.getImage(), elaborateCartItemDTO.getProduct_image());
                assertEquals(productDTO.getPrice(), elaborateCartItemDTO.getProduct_price());
                when(productService.getProductById(productDTO.getId()))
                        .thenReturn(new ResponseEntity<>(productDTO, HttpStatus.OK));
            }

            OrderServiceException exception = assertThrows(OrderServiceException.class,
                    () -> orderService.orderUserCart(user_id, orderCartRequest));

            verify(cartService, times(1))
                    .getCartByUser(anyLong());

            for(int i = 0; i < productDTOList.size(); i++) {
                ProductDTO productDTO = productDTOList.get(i);
                verify(productService, times(1))
                        .getProductById(productDTO.getId());
            }

            assertEquals("Not enough " + problematic_product.getName() + " in stock", exception.getMessage());
            assertEquals("ORDER_CANNOT_BE_ISSUED", exception.getError_code());
            assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
        }

        List<ElaborateCartItemDTO> getElaborateCartItemDTOList() {
            List<ElaborateCartItemDTO> cart = new ArrayList<>();
            List<ProductDTO> productDTOList = getProductDTOList();
            for(int i = 0; i < productDTOList.size(); i++){
                ProductDTO productDTO = productDTOList.get(i);
                cart.add(ElaborateCartItemDTO.builder()
                        .cart_item_id(i)
                        .product_name(productDTO.getName())
                        .cart_item_count(productDTO.getQuantity() - 1)
                        .product_price(i)
                        .product_id(productDTO.getId())
                        .product_price(productDTO.getPrice())
                        .product_name(productDTO.getName())
                        .product_image(productDTO.getImage())
                        .build());
            }
            return cart;
        }

        OrderCartRequest getOrderCartRequestMock() {
            return OrderCartRequest.builder()
                    .firstName("firstName")
                    .lastName("lastName")
                    .phoneNumber("phoneNumber")
                    .country("country")
                    .city("city")
                    .postalCode("postalCode")
                    .street("street")
                    .houseNumber("houseNumber")
                    .build();
        }
    }

    @Nested
    @DisplayName("getOrders")
    class getOrders {

        @Test
        @DisplayName("success")
        void success() {
            int user_id = 1;

            List<Order> orderList = getOrderList(user_id);
            orderList.get(0).setOrderItems(getOrderItemList1());
            orderList.get(1).setOrderItems(getOrderItemList2());
            when(orderRepository.findByUserIdOrderByTimeDesc(user_id))
                    .thenReturn(orderList);
            List<List<OrderItem>> orderItemsList = new ArrayList<>();
            orderItemsList.add(getOrderItemList1());
            orderItemsList.add(getOrderItemList2());
            for(int i = 0; i < orderList.size(); i++) {
                Order order = orderList.get(i);
                List<OrderItem> orderItemList = orderItemsList.get(i);
                when(orderItemRepository.findAllByOrderId(order.getId()))
                        .thenReturn(orderItemList);
            }
            List<ProductDTO> productDTOList = getProductDTOList();
            List<ProductDTO> productDTOList2 = getProductDTOList2();
            when(productService.getProductById(1))
                    .thenReturn(new ResponseEntity<>(productDTOList.get(0), HttpStatus.OK));
            when(productService.getProductById(2))
                    .thenReturn(new ResponseEntity<>(productDTOList.get(1), HttpStatus.OK));
            when(productService.getProductById(3))
                    .thenReturn(new ResponseEntity<>(productDTOList2.get(0), HttpStatus.OK));
            when(productService.getProductById(4))
                    .thenReturn(new ResponseEntity<>(productDTOList2.get(1), HttpStatus.OK));

            List<ElaborateOrderDTO> elaborateOrderDTOList = orderService.getOrders(user_id);

            verify(orderRepository, times(1))
                    .findByUserIdOrderByTimeDesc(anyLong());
            verify(productService, times(4))
                    .getProductById(anyLong());

            for(int i = 0; i < elaborateOrderDTOList.size(); i++){
                ElaborateOrderDTO elaborateOrderDTO = elaborateOrderDTOList.get(i);
                Order order = orderList.get(i);
                assertEquals(order.getFirstName(), elaborateOrderDTO.getFirstName());
                assertEquals(order.getLastName(), elaborateOrderDTO.getLastName());
                assertEquals(order.getPhoneNumber(), elaborateOrderDTO.getPhoneNumber());
                assertEquals(order.getCountry(), elaborateOrderDTO.getCountry());
                assertEquals(order.getCity(), elaborateOrderDTO.getCity());
                assertEquals(order.getPostalCode(), elaborateOrderDTO.getPostalCode());
                assertEquals(order.getStreet(), elaborateOrderDTO.getStreet());
                assertEquals(order.getHouseNumber(), elaborateOrderDTO.getHouseNumber());
                assertEquals(order.getPrice(), elaborateOrderDTO.getPrice());
                for(int j = 0; j < elaborateOrderDTO.getElaborateOrderItems().size(); j++){
                    ElaborateOrderItemDTO elaborateOrderItemDTO = elaborateOrderDTO.getElaborateOrderItems().get(j);
                    List<OrderItem> orderItemList = orderItemsList.get(i);
                    List<ProductDTO> productDTOs;
                    if(i == 0)
                        productDTOs = productDTOList;
                    else
                        productDTOs = productDTOList2;
                    assertEquals(elaborateOrderItemDTO.getCount(), orderItemList.get(j).getCount());
                    assertEquals(elaborateOrderItemDTO.getPrice(), productDTOs.get(j).getPrice());
                    assertEquals(elaborateOrderItemDTO.getProduct_name(), productDTOs.get(j).getName());
                    assertEquals(elaborateOrderItemDTO.getProduct_id(), productDTOs.get(j).getId());
                }
            }
        }

        List<Order> getOrderList(int user_id) {
            List<Order> orderList = new ArrayList<>();
            Order order1 = Order.builder()
                    .id(1)
                    .userId(user_id)
                    .country("country1")
                    .city("city1")
                    .street("street1")
                    .postalCode("postalCode1")
                    .houseNumber("houseNumber1")
                    .firstName("firstName1")
                    .lastName("lastName1")
                    .phoneNumber("phoneNumber1")
                    .time(Instant.now())
                    .build();
            orderList.add(order1);
            Order order2 = Order.builder()
                    .id(2)
                    .userId(user_id)
                    .country("country2")
                    .city("city2")
                    .street("street2")
                    .postalCode("postalCode2")
                    .houseNumber("houseNumber2")
                    .firstName("firstName2")
                    .lastName("lastName2")
                    .phoneNumber("phoneNumber2")
                    .time(Instant.now())
                    .build();
            orderList.add(order2);
            return orderList;
        }

        List<OrderItem> getOrderItemList1() {
            List<Order> orders = getOrderList(1);

            OrderItem orderItem1 = OrderItem.builder()
                    .id(1)
                    .count(1)
                    .productId(1)
                    .order(orders.get(0))
                    .build();
            OrderItem orderItem2 = OrderItem.builder()
                    .id(2)
                    .count(2)
                    .productId(2)
                    .order(orders.get(1))
                    .build();
            return List.of(orderItem1, orderItem2);
        }

        List<OrderItem> getOrderItemList2() {
            List<Order> orders = getOrderList(2);
            OrderItem orderItem1 = OrderItem.builder()
                    .id(3)
                    .count(3)
                    .productId(3)
                    .order(orders.get(0))
                    .build();
            OrderItem orderItem2 = OrderItem.builder()
                    .id(4)
                    .count(4)
                    .productId(4)
                    .order(orders.get(1))
                    .build();
            return List.of(orderItem1, orderItem2);
        }
    }

    List<ProductDTO> getProductDTOList() {
        List<ProductDTO> productDTOList = new ArrayList<>();
        productDTOList.add(ProductDTO.builder()
                .id(1)
                .price(1)
                .image("image1")
                .name("name1")
                .description("description1")
                .for_animal("for_animal1")
                .utility("utility1")
                .quantity(5)
                .build());
        productDTOList.add(ProductDTO.builder()
                .id(2)
                .price(2)
                .image("image2")
                .name("name2")
                .description("description2")
                .for_animal("for_animal2")
                .utility("utility2")
                .quantity(10)
                .build());
        return productDTOList;
    }

    List<ProductDTO> getProductDTOList2() {
        List<ProductDTO> productDTOList = new ArrayList<>();
        productDTOList.add(ProductDTO.builder()
                .id(3)
                .price(3)
                .image("image3")
                .name("name3")
                .description("description3")
                .for_animal("for_animal3")
                .utility("utility3")
                .quantity(3)
                .build());
        productDTOList.add(ProductDTO.builder()
                .id(4)
                .price(4)
                .image("image4")
                .name("name4")
                .description("description4")
                .for_animal("for_animal4")
                .utility("utility4")
                .quantity(4)
                .build());
        return productDTOList;
    }
}