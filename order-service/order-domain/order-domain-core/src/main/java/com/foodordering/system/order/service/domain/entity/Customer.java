package com.foodordering.system.order.service.domain.entity;

import com.foodordering.system.domain.entity.AggregateRoot;
import com.foodordering.system.domain.valueobject.CustomerId;

public class Customer extends AggregateRoot<CustomerId> {
    public Customer() {
    }

    public Customer(CustomerId customerId) {
        super.setId(customerId);
    }
}
