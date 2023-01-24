package com.foodordering.system.order.service.domain.ports.output.repository;

import com.foodordering.system.domain.valueobject.OrderId;
import com.foodordering.system.order.service.domain.entity.Order;
import com.foodordering.system.order.service.domain.valueobject.TrackingId;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findByTrackingId(TrackingId trackingId);

    Optional<Order> findById(OrderId orderId);
}
