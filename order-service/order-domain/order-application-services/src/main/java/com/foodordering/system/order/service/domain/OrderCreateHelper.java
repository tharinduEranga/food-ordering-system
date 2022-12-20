package com.foodordering.system.order.service.domain;

import com.foodordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.foodordering.system.order.service.domain.entity.Customer;
import com.foodordering.system.order.service.domain.entity.Order;
import com.foodordering.system.order.service.domain.entity.Restaurant;
import com.foodordering.system.order.service.domain.event.OrderCreatedEvent;
import com.foodordering.system.order.service.domain.exception.OrderDomainException;
import com.foodordering.system.order.service.domain.mapper.OrderDataMapper;
import com.foodordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.foodordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.foodordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Component
public class OrderCreateHelper {
    private final OrderDomainService orderDomainService;

    private final OrderRepository orderRepository;

    private final CustomerRepository customerRepository;

    private final RestaurantRepository restaurantRepository;

    private final OrderDataMapper orderDataMapper;

    @Transactional
    public OrderCreatedEvent persistOrder(CreateOrderCommand createOrderCommand) {
        checkCustomer(createOrderCommand.getCustomerId());
        Restaurant restaurant = checkRestaurant(createOrderCommand);
        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitiateOrder(order, restaurant);
        Order orderResult = saveOrder(order);
        log.info("Order is created with id: {}", orderCreatedEvent.getOrder().getId().getValue());
        return orderCreatedEvent;
    }


    private void checkCustomer(UUID customerId) {
        Optional<Customer> optionalCustomer = customerRepository.findCustomer(customerId);
        if (optionalCustomer.isEmpty()) {
            log.warn("Could not find customer with id: {}", customerId);
            throw new OrderDomainException("Could not find customer with id: " + customerId);
        }
    }

    private Restaurant checkRestaurant(CreateOrderCommand createOrderCommand) {
        Restaurant restaurant = orderDataMapper.createOrderCommand(createOrderCommand);
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findRestaurantInformation(restaurant);
        if (optionalRestaurant.isEmpty()) {
            log.warn("Could not find restaurant with id: {}", createOrderCommand.getRestaurantId());
            throw new OrderDomainException("Could not find restaurant with id: " + createOrderCommand.getRestaurantId());
        }
        return optionalRestaurant.get();
    }

    private Order saveOrder(Order order) {
        Order orderResult = orderRepository.save(order);
        if (orderResult == null) {
            log.error("Order save failed with id: {}", order.getId());
            throw new OrderDomainException("Could not save order!");
        }
        log.info("Order is saved with id: {}", order.getId());
        return orderResult;
    }

}
