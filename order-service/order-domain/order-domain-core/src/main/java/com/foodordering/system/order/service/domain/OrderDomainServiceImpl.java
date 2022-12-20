package com.foodordering.system.order.service.domain;

import com.foodordering.system.order.service.domain.entity.Order;
import com.foodordering.system.order.service.domain.entity.OrderItem;
import com.foodordering.system.order.service.domain.entity.Product;
import com.foodordering.system.order.service.domain.entity.Restaurant;
import com.foodordering.system.order.service.domain.event.OrderCancelledEvent;
import com.foodordering.system.order.service.domain.event.OrderCreatedEvent;
import com.foodordering.system.order.service.domain.event.OrderPaidEvent;
import com.foodordering.system.order.service.domain.exception.OrderDomainException;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class OrderDomainServiceImpl implements OrderDomainService {

    private static final String UTC = "UTC";

    @Override
    public OrderCreatedEvent validateAndInitiateOrder(Order order, Restaurant restaurant) {
        validateRestaurant(restaurant);
        setOrderProductInformation(order, restaurant);
        order.validateOrder();
        order.initializeOrder();
        log.info("Order with id: {} is initiated", order.getId().getValue());
        return new OrderCreatedEvent(order, ZonedDateTime.now(ZoneId.of(UTC)));
    }

    @Override
    public OrderPaidEvent payOrder(Order order) {
        order.pay();
        log.info("Order with id: {} is paid", order.getId().getValue());
        return new OrderPaidEvent(order, ZonedDateTime.now(ZoneId.of(UTC)));
    }

    @Override
    public void approveOrder(Order order) {
        order.pay();
        log.info("Order with id: {} is approved", order.getId().getValue());
    }

    @Override
    public OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages) {
        order.initCancel(failureMessages);
        log.info("Order with id: {} is cancelling", order.getId().getValue());
        return new OrderCancelledEvent(order, ZonedDateTime.now(ZoneId.of(UTC)));
    }

    @Override
    public void cancelOrder(Order order, List<String> failureMessages) {
        order.cancel(failureMessages);
        log.info("Order with id: {} is cancelled", order.getId().getValue());
    }


    private void validateRestaurant(Restaurant restaurant) {
        if (!restaurant.isActive()) {
            throw new OrderDomainException("Restaurant with id " + restaurant.getId().getValue() +
                    " is currently not active!");
        }
    }

    private void setOrderProductInformation(Order order, Restaurant restaurant) {
        /*optimize the method to linear complexity by introducing a new data structure*/
        Map<Product, Product> restaurantProducts = restaurant.getProducts().stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        restaurantProduct -> restaurantProduct,
                        (a, b) -> b)
                );
        order.getItems().stream().map(OrderItem::getProduct).forEach(currentProduct -> {
            Product restaurantProduct = restaurantProducts.get(currentProduct);
            if (restaurantProduct != null) {
                currentProduct.updateWithConfirmedNameAndPrice(
                        restaurantProduct.getName(),
                        restaurantProduct.getPrice());
            }
        });
    }
}
