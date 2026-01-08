package com.islamhamada.petshop.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.DateTimeUnit;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.islamhamada.petshop.OrderServiceConfig;
import com.islamhamada.petshop.contracts.dto.ElaborateCartItemDTO;
import com.islamhamada.petshop.contracts.dto.ElaborateOrderDTO;
import com.islamhamada.petshop.contracts.dto.ElaborateOrderItemDTO;
import com.islamhamada.petshop.entity.Order;
import com.islamhamada.petshop.entity.OrderItem;
import com.islamhamada.petshop.model.OrderCartRequest;
import com.islamhamada.petshop.repository.OrderItemRepository;
import com.islamhamada.petshop.repository.OrderRepository;
import org.h2.util.DateTimeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.DATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest({
        "server.port=0",
        "product-service-svc.url=http://localhost:9090",
        "cart-service-svc.url=http://localhost:9090"
})
@EnableConfigurationProperties
@AutoConfigureMockMvc
@ContextConfiguration(classes = {OrderServiceConfig.class})
class OrderControllerTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    MockMvc mockMvc;

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension
            .newInstance()
            .options(WireMockConfiguration
                    .wireMockConfig()
                    .port(9090))
            .build();

    ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    SimpleGrantedAuthority adminRole = new SimpleGrantedAuthority("ROLE_Admin");
    SimpleGrantedAuthority customerRole = new SimpleGrantedAuthority("ROLE_Customer");
    SimpleGrantedAuthority neededRole = customerRole;
    SimpleGrantedAuthority notNeededRole = adminRole;

    @BeforeEach
    public void setup() throws IOException {
        orderItemRepository.deleteAll();
        getProductById();
        getCartByUser();
        
    }

    private void getProductById() throws IOException {
        wireMockServer.stubFor(WireMock.get("/product/1")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(StreamUtils.copyToString(
                                OrderControllerTest.class.getClassLoader()
                                        .getResourceAsStream("mock/GetProduct1.json"),
                                Charset.defaultCharset()
                        ))));
        wireMockServer.stubFor(WireMock.get("/product/2")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(StreamUtils.copyToString(
                                OrderControllerTest.class.getClassLoader()
                                        .getResourceAsStream("mock/GetProduct2.json"),
                                Charset.defaultCharset()
                        ))));
        wireMockServer.stubFor(WireMock.get("/product/999")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())));
    }

    private void getCartByUser() throws IOException {
        wireMockServer.stubFor(WireMock.get("/cart/1")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withBody(StreamUtils.copyToString(
                                OrderControllerTest.class.getClassLoader()
                                        .getResourceAsStream("mock/GetCartByUser.json"),
                                Charset.defaultCharset()
                        ))));
        wireMockServer.stubFor(WireMock.get("/cart/999")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())));
    }

    @Nested
    public class orderUserCart{

        @Test
        public void success() {

        }

        @Test
        public void failure_no_permission() throws Exception {
            long user_id = 1;
            OrderCartRequest request = getMockOrderCartRequest();
            MvcResult mvcResult = mockMvc.perform(post("/order/" + user_id)
                    .with(jwt().authorities(notNeededRole))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden())
                    .andReturn();
        }

        @ParameterizedTest
        @MethodSource("bad_input")
        public void failure_bad_input(long user_id, OrderCartRequest request) throws Exception {
            MvcResult mvcResult = mockMvc.perform(post("/order/" + user_id)
                            .with(jwt().authorities(neededRole))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        }

        public static List<Arguments> bad_input() {
            List<Arguments> list = new ArrayList<>();
            list.add(Arguments.of(-1, getMockOrderCartRequest()));

            OrderCartRequest request11 = getMockOrderCartRequest();
            request11.setFirstName(null);
            list.add(Arguments.of(1, request11));
            OrderCartRequest request12 = getMockOrderCartRequest();
            request12.setFirstName("");
            list.add(Arguments.of(1, request12));
            OrderCartRequest request13 = getMockOrderCartRequest();
            request13.setFirstName("  ");
            list.add(Arguments.of(1, request13));

            OrderCartRequest request21 = getMockOrderCartRequest();
            request21.setPhoneNumber("-201293567");
            list.add(Arguments.of(1, request21));
            OrderCartRequest request22 = getMockOrderCartRequest();
            request22.setPhoneNumber("12345678");
            list.add(Arguments.of(1, request22));
            OrderCartRequest request23 = getMockOrderCartRequest();
            request23.setPhoneNumber("0123456");
            list.add(Arguments.of(1, request23));
            OrderCartRequest request24 = getMockOrderCartRequest();
            request24.setPhoneNumber("01234 567");
            list.add(Arguments.of(1, request24));

            OrderCartRequest request31 = getMockOrderCartRequest();
            request31.setPostalCode("01342");
            list.add(Arguments.of(1, request31));
            OrderCartRequest request32 = getMockOrderCartRequest();
            request32.setPostalCode("12");
            list.add(Arguments.of(1, request32));
            OrderCartRequest request33 = getMockOrderCartRequest();
            request33.setPostalCode("1120a");
            list.add(Arguments.of(1, request33));

            return list;
        }

        private static OrderCartRequest getMockOrderCartRequest() {
            return OrderCartRequest.builder()
                    .firstName("firstName")
                    .lastName("lastName")
                    .country("country")
                    .city("city")
                    .street("street")
                    .houseNumber("2")
                    .postalCode("1120")
                    .phoneNumber("04834394")
                    .build();
        }
    }

    @Nested
    public class getOrders {

        @Test
        public void success() throws Exception {
            long user_id = 1;

            Order order1 = orderRepository.save(Order.builder()
                    .price(5)
                    .time(Instant.now())
                    .firstName("firstName1")
                    .lastName("lastName1")
                    .country("country1")
                    .city("city1")
                    .street("street1")
                    .houseNumber("12")
                    .postalCode("1234")
                    .userId(user_id)
                    .build());

            OrderItem orderItem = orderItemRepository.save(OrderItem.builder()
                    .count(3)
                    .productId(1)
                    .orderId(order1.getId())
                    .build());
            OrderItem orderItem2 = orderItemRepository.save(OrderItem.builder()
                    .count(4)
                    .productId(2)
                    .orderId(order1.getId())
                    .build());

            Order order2 = orderRepository.save(Order.builder()
                    .price(10)
                    .time(Instant.now())
                    .firstName("firstName2")
                    .lastName("lastName2")
                    .country("country2")
                    .city("city2")
                    .street("street2")
                    .houseNumber("13")
                    .postalCode("12345")
                    .userId(user_id)
                    .build());

            OrderItem orderItem3 = orderItemRepository.save(OrderItem.builder()
                    .count(10)
                    .productId(1)
                    .orderId(order2.getId())
                    .build());

            MvcResult mvcResult = mockMvc.perform(get("/order/" + user_id)
                    .with(jwt().authorities(neededRole)))
                    .andExpect(status().isOk())
                    .andReturn();
            String response = mvcResult.getResponse().getContentAsString();
            List<ElaborateOrderDTO> elaborateOrderDTOs = objectMapper.readValue(response, new TypeReference<List<ElaborateOrderDTO>>(){});
            assertEquals(2, elaborateOrderDTOs.size());

            ElaborateOrderDTO elaborateOrder1 = elaborateOrderDTOs.get(1);
            assertEquals(elaborateOrder1.getFirstName(), order1.getFirstName());
            assertEquals(elaborateOrder1.getLastName(), order1.getLastName());
            assertEquals(elaborateOrder1.getCountry(), order1.getCountry());
            assertEquals(elaborateOrder1.getCity(), order1.getCity());
            assertEquals(elaborateOrder1.getStreet(), order1.getStreet());
            assertEquals(elaborateOrder1.getHouseNumber(), order1.getHouseNumber());
            assertEquals(elaborateOrder1.getPostalCode(), order1.getPostalCode());
            assertEquals(elaborateOrder1.getPrice(), order1.getPrice());
            assertEquals(elaborateOrder1.getPhoneNumber(), order1.getPhoneNumber());
            assertEquals(elaborateOrder1.getTime().toEpochMilli(), order1.getTime().toEpochMilli());

            List<ElaborateOrderItemDTO> elaborateOrderItemDTO = elaborateOrder1.getElaborateOrderItems();
            ElaborateOrderItemDTO orderItemDTO = elaborateOrderItemDTO.get(0);
            assertEquals(orderItemDTO.getCount(), orderItem.getCount());
            assertEquals(orderItemDTO.getProduct_id(), orderItem.getProductId());
            assertEquals(order1.getId(), orderItem.getOrderId());
            ElaborateOrderItemDTO orderItemDTO2 = elaborateOrderItemDTO.get(1);
            assertEquals(orderItemDTO2.getCount(), orderItem2.getCount());
            assertEquals(orderItemDTO2.getProduct_id(), orderItem2.getProductId());
            assertEquals(order1.getId(), orderItem2.getOrderId());

            ElaborateOrderDTO elaborateOrder2 = elaborateOrderDTOs.get(0);
            assertEquals(elaborateOrder2.getFirstName(), order2.getFirstName());
            assertEquals(elaborateOrder2.getLastName(), order2.getLastName());
            assertEquals(elaborateOrder2.getCountry(), order2.getCountry());
            assertEquals(elaborateOrder2.getCity(), order2.getCity());
            assertEquals(elaborateOrder2.getStreet(), order2.getStreet());
            assertEquals(elaborateOrder2.getHouseNumber(), order2.getHouseNumber());
            assertEquals(elaborateOrder2.getPostalCode(), order2.getPostalCode());
            assertEquals(elaborateOrder2.getPrice(), order2.getPrice());
            assertEquals(elaborateOrder2.getPhoneNumber(), order2.getPhoneNumber());
            assertEquals(elaborateOrder2.getTime().toEpochMilli(), order2.getTime().toEpochMilli());

            List<ElaborateOrderItemDTO> elaborateOrderItemDTOs = elaborateOrder2.getElaborateOrderItems();
            ElaborateOrderItemDTO orderItemDTO3 = elaborateOrderItemDTOs.get(0);
            assertEquals(orderItemDTO3.getCount(), orderItem3.getCount());
            assertEquals(orderItemDTO3.getProduct_id(), orderItem3.getProductId());
            assertEquals(order2.getId(), orderItem3.getOrderId());
        }

        @Test
        public void failure_no_permission() throws Exception {
            long user_id = 1;
            MvcResult mvcResult = mockMvc.perform(get("/order/" + user_id)
                    .with(jwt().authorities(notNeededRole)))
                    .andExpect(status().isForbidden())
                    .andReturn();
        }

        @Test
        public void failure_bad_input() throws Exception {
            long user_id = -1;
            MvcResult mvcResult = mockMvc.perform(get("/order/" + user_id)
                    .with(jwt().authorities(notNeededRole)))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        }
    }
}