package com.foodordering.system.order.service.domain;

import com.foodordering.system.domain.valueobject.OrderId;
import com.foodordering.system.order.service.domain.entity.Order;
import com.foodordering.system.order.service.domain.exception.OrderNotFoundException;
import com.foodordering.system.order.service.domain.ports.output.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderSagaHelper {

    private final OrderRepository orderRepository;

    Order findOrder(String orderId) {
        Optional<Order> orderOptional = orderRepository.findById(new OrderId(UUID.fromString(orderId)));
        if (orderOptional.isEmpty()) {
            log.info("Order with id: {} could not be found!", orderId);
            throw new OrderNotFoundException("Order with id: " + orderId + " could not be found!");
        }
        return orderOptional.get();
    }

    void saveOrder(Order order) {
        orderRepository.save(order);
    }
}
