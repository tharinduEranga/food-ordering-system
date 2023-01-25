package com.foodordering.system.order.service.domain;

import com.foodordering.system.domain.event.EmptyEvent;
import com.foodordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import com.foodordering.system.order.service.domain.entity.Order;
import com.foodordering.system.order.service.domain.event.OrderCancelledEvent;
import com.foodordering.system.order.service.domain.ports.output.repository.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import com.foodordering.system.saga.SagaStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderApprovalSaga implements SagaStep<RestaurantApprovalResponse, EmptyEvent, OrderCancelledEvent> {

    private final OrderDomainService orderDomainService;
    private final OrderSagaHelper orderSagaHelper;
    private final OrderCancelledPaymentRequestMessagePublisher orderCancelledPaymentRequestMessagePublisher;

    @Transactional
    @Override
    public EmptyEvent process(RestaurantApprovalResponse restaurantApprovalResponse) {
        log.info("Approving order with id: {}", restaurantApprovalResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(restaurantApprovalResponse.getOrderId());
        orderDomainService.approveOrder(order);
        orderSagaHelper.saveOrder(order);
        log.info("Order with id: {} is approved", order.getId().getValue());
        return EmptyEvent.INSTANCE;
    }

    @Transactional
    @Override
    public OrderCancelledEvent rollback(RestaurantApprovalResponse restaurantApprovalResponse) {
        log.info("Cancelling order with id: {}", restaurantApprovalResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(restaurantApprovalResponse.getOrderId());
        OrderCancelledEvent orderCancelledEvent = orderDomainService.cancelOrderPayment(order,
                restaurantApprovalResponse.getFailureMessages(), orderCancelledPaymentRequestMessagePublisher);
        orderSagaHelper.saveOrder(order);
        log.info("Order with id: {} is cancelling", order.getId().getValue());
        return orderCancelledEvent;
    }
}
