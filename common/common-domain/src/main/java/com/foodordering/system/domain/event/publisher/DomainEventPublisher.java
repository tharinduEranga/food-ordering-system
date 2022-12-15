package com.foodordering.system.domain.event.publisher;

import com.foodordering.system.domain.event.DomainEvent;

public interface DomainEventPublisher<T extends DomainEvent> {

    void publish(T domainEvent);
}
