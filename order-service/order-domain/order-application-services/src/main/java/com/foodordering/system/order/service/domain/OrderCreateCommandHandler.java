package com.foodordering.system.order.service.domain;

import com.foodordering.system.order.service.domain.dto.OrderCreateHelper;
import com.foodordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.foodordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.foodordering.system.order.service.domain.event.OrderCreatedEvent;
import com.foodordering.system.order.service.domain.mapper.OrderDataMapper;
import com.foodordering.system.order.service.domain.ports.output.repository.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class OrderCreateCommandHandler {

    private final OrderDataMapper orderDataMapper;

    private final OrderCreateHelper orderCreateHelper;

    private final OrderCreatedPaymentRequestMessagePublisher orderCreatedPaymentRequestMessagePublisher;

    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
        OrderCreatedEvent orderCreatedEvent = orderCreateHelper.persistOrder(createOrderCommand);
        log.info("Order is created with id: {}", orderCreatedEvent.getOrder().getId().getValue());
        orderCreatedPaymentRequestMessagePublisher.publish(orderCreatedEvent);
        return orderDataMapper.orderToCreateOrderResponse(orderCreatedEvent.getOrder());
    }


}
