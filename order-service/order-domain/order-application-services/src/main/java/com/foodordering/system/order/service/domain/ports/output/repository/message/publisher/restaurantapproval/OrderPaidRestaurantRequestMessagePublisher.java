package com.foodordering.system.order.service.domain.ports.output.repository.message.publisher.restaurantapproval;

import com.foodordering.system.domain.event.publisher.DomainEventPublisher;
import com.foodordering.system.order.service.domain.event.OrderPaidEvent;

public interface OrderPaidRestaurantRequestMessagePublisher extends DomainEventPublisher<OrderPaidEvent> {

}
