package com.foodordering.system.order.service.domain.valueobject;

import com.foodordering.system.domain.valueobject.BaseId;

public class OrderItemId extends BaseId<Long> {
    public OrderItemId(Long value) {
        super(value);
    }
}
