package com.foodordering.system.payment.service.domain.ports.output.message.publisher;

import com.foodordering.system.domain.event.publisher.DomainEventPublisher;
import com.foodordering.system.payment.service.domain.event.PaymentFailedEvent;

public interface PaymentFailedMessagePublisher extends DomainEventPublisher<PaymentFailedEvent> {
    
}
