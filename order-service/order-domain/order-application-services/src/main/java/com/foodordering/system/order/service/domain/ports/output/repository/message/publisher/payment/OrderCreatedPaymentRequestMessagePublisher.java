package com.foodordering.system.order.service.domain.ports.output.repository.message.publisher.payment;

import com.foodordering.system.domain.event.publisher.DomainEventPublisher;
import com.foodordering.system.order.service.domain.event.OrderCreatedEvent;

public interface OrderCreatedPaymentRequestMessagePublisher extends DomainEventPublisher<OrderCreatedEvent> {

}
