package com.foodordering.system.order.service.domain;

import com.foodordering.system.domain.valueobject.*;
import com.foodordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.foodordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.foodordering.system.order.service.domain.dto.create.OrderAddress;
import com.foodordering.system.order.service.domain.dto.create.OrderItem;
import com.foodordering.system.order.service.domain.entity.Customer;
import com.foodordering.system.order.service.domain.entity.Order;
import com.foodordering.system.order.service.domain.entity.Product;
import com.foodordering.system.order.service.domain.entity.Restaurant;
import com.foodordering.system.order.service.domain.exception.OrderDomainException;
import com.foodordering.system.order.service.domain.mapper.OrderDataMapper;
import com.foodordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import com.foodordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.foodordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.foodordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) //otherwise it will create new one for each test method
@SpringBootTest(classes = OrderTestConfiguration.class)
public class OrderApplicationServiceTest {

    @Autowired
    private OrderApplicationService orderApplicationService;

    @Autowired
    private OrderDataMapper orderDataMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    private CreateOrderCommand createOrderCommand;
    private CreateOrderCommand createOrderCommandWrongPrice;
    private CreateOrderCommand createOrderCommandWrongProductPrice;

    private final UUID CUSTOMER_ID = UUID.randomUUID();
    private final UUID RESTAURANT_ID = UUID.randomUUID();
    private final UUID PRODUCT_ID = UUID.randomUUID();
    private final UUID ORDER_ID = UUID.randomUUID();
    private final BigDecimal PRICE = new BigDecimal("200.00");

    @BeforeAll
    public void init() {
        createOrderCommand = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddress.builder()
                        .street("street_1")
                        .postalCode("100AB")
                        .city("Dubai")
                        .build())
                .price(PRICE)
                .items(List.of(
                                OrderItem.builder()
                                        .productId(PRODUCT_ID)
                                        .quantity(1)
                                        .price(new BigDecimal("50.00"))
                                        .subTotal(new BigDecimal("50.00"))
                                        .build(),
                                OrderItem.builder()
                                        .productId(PRODUCT_ID)
                                        .quantity(3)
                                        .price(new BigDecimal("50.00"))
                                        .subTotal(new BigDecimal("150.00"))
                                        .build()
                        )
                )
                .build();
        createOrderCommandWrongPrice = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddress.builder()
                        .street("street_1")
                        .postalCode("100AB")
                        .city("Dubai")
                        .build())
                .price(new BigDecimal("250.00"))
                .items(List.of(
                                OrderItem.builder()
                                        .productId(PRODUCT_ID)
                                        .quantity(2)
                                        .price(new BigDecimal("50.00"))
                                        .subTotal(new BigDecimal("100.00"))
                                        .build(),
                                OrderItem.builder()
                                        .productId(PRODUCT_ID)
                                        .quantity(1)
                                        .price(new BigDecimal("50.00"))
                                        .subTotal(new BigDecimal("50.00"))
                                        .build()
                        )
                )
                .build();
        createOrderCommandWrongProductPrice = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddress.builder()
                        .street("street_1")
                        .postalCode("100AB")
                        .city("Dubai")
                        .build())
                .price(new BigDecimal("210.00"))
                .items(List.of(
                                OrderItem.builder()
                                        .productId(PRODUCT_ID)
                                        .quantity(2)
                                        .price(new BigDecimal("60.00"))
                                        .subTotal(new BigDecimal("60.00"))
                                        .build(),
                                OrderItem.builder()
                                        .productId(PRODUCT_ID)
                                        .quantity(3)
                                        .price(new BigDecimal("50.00"))
                                        .subTotal(new BigDecimal("150.00"))
                                        .build()
                        )
                )
                .build();

        Customer customer = new Customer();
        customer.setId(new CustomerId(CUSTOMER_ID));

        Restaurant restaurantResponse = Restaurant.builder()
                .restaurantId(new RestaurantId(RESTAURANT_ID))
                .products(List.of(
                        new Product(new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.0"))),
                        new Product(new ProductId(PRODUCT_ID), "product-2", new Money(new BigDecimal("50.0")))
                ))
                .active(true)
                .build();

        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        order.setId(new OrderId<>(ORDER_ID));

        when(customerRepository.findCustomer(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(restaurantRepository.findRestaurantInformation(
                orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
                .thenReturn(Optional.of(restaurantResponse));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
    }

    @Test
    public void testCreateOrder() {
        CreateOrderResponse createOrderResponse = orderApplicationService.createOrder(createOrderCommand);
        assertEquals(OrderStatus.PENDING, createOrderResponse.getOrderStatus());
        assertEquals("Order Created Successfully", createOrderResponse.getMessage());
        assertNotNull(createOrderResponse.getOrderTrackingId());
    }

    @Test
    public void testCreateOrderWithWrongTotalPrice() {
        OrderDomainException orderDomainException = assertThrows(OrderDomainException.class, () -> {
            orderApplicationService.createOrder(createOrderCommandWrongPrice);
        });
        assertEquals("Total price: " + createOrderCommandWrongPrice.getPrice()
                + " is not equal to Order items total: 150.00", orderDomainException.getMessage());
    }

    @Test
    public void testCreateOrderWithWrongProductPrice() {
        OrderDomainException orderDomainException = assertThrows(OrderDomainException.class, () -> {
            orderApplicationService.createOrder(createOrderCommandWrongProductPrice);
        });
        assertEquals(orderDomainException.getMessage(), "Order item price: 60.00" +
                " is not valid for product :" + PRODUCT_ID);
    }

    @Test
    public void testCreateOrderWithPassiveRestaurant() {
        Restaurant restaurantResponse = Restaurant.builder()
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .products(List.of(
                        new Product(new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.0"))),
                        new Product(new ProductId(PRODUCT_ID), "product-2", new Money(new BigDecimal("50.0")))
                ))
                .active(false)
                .build();
        when(restaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
                .thenReturn(Optional.of(restaurantResponse));
        OrderDomainException orderDomainException = assertThrows(OrderDomainException.class, () -> orderApplicationService.createOrder(createOrderCommand));
        assertEquals("Restaurant with id " + RESTAURANT_ID + " is currently not active!", orderDomainException.getMessage());
    }
}
