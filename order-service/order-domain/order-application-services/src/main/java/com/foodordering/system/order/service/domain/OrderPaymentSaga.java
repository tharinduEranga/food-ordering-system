package com.foodordering.system.order.service.domain;

import com.foodordering.system.domain.event.EmptyEvent;
import com.foodordering.system.order.service.domain.dto.message.PaymentResponse;
import com.foodordering.system.order.service.domain.entity.Order;
import com.foodordering.system.order.service.domain.event.OrderPaidEvent;
import com.foodordering.system.order.service.domain.ports.output.repository.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import com.foodordering.system.saga.SagaStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderPaymentSaga implements SagaStep<PaymentResponse, OrderPaidEvent, EmptyEvent> {

    private final OrderDomainService orderDomainService;
    private final OrderSagaHelper orderSagaHelper;
    private final OrderPaidRestaurantRequestMessagePublisher orderPaidRestaurantRequestMessagePublisher;

    @Transactional
    @Override
    public OrderPaidEvent process(PaymentResponse paymentResponse) {
        log.info("Completing payment for order with id: {}", paymentResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(paymentResponse.getOrderId());
        OrderPaidEvent orderPaidEvent = orderDomainService.payOrder(order, orderPaidRestaurantRequestMessagePublisher);
        orderSagaHelper.saveOrder(order);
        log.info("Order is paid with id: {}", order.getId().getValue());
        return orderPaidEvent;
    }

    @Transactional
    @Override
    public EmptyEvent rollback(PaymentResponse paymentResponse) {
        log.info("Cancelling payment for order with id: {}", paymentResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(paymentResponse.getOrderId());
        orderDomainService.cancelOrder(order, paymentResponse.getFailureMessages());
        orderSagaHelper.saveOrder(order);
        log.info("Order is cancelled with id: {}", order.getId().getValue());
        return EmptyEvent.INSTANCE;
    }

}
