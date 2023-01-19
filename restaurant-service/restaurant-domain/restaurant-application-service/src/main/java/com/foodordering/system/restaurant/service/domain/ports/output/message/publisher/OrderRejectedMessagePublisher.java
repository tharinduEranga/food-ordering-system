package com.foodordering.system.restaurant.service.domain.ports.output.message.publisher;

import com.foodordering.system.domain.event.publisher.DomainEventPublisher;
import com.foodordering.system.restaurant.service.domain.event.OrderApprovedEvent;
import com.foodordering.system.restaurant.service.domain.event.OrderRejectedEvent;

public interface OrderRejectedMessagePublisher extends DomainEventPublisher<OrderRejectedEvent> {

}
