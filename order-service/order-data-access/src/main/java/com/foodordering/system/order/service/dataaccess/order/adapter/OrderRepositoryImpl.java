package com.foodordering.system.order.service.dataaccess.order.adapter;

import com.foodordering.system.order.service.dataaccess.order.entity.OrderEntity;
import com.foodordering.system.order.service.dataaccess.order.mapper.OrderDataAccessMapper;
import com.foodordering.system.order.service.dataaccess.order.repository.OrderJpaRepository;
import com.foodordering.system.order.service.domain.entity.Order;
import com.foodordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.foodordering.system.order.service.domain.valueobject.TrackingId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderDataAccessMapper orderDataAccessMapper;

    @Override
    public Order save(Order order) {
        OrderEntity savedOrder = orderJpaRepository.save(orderDataAccessMapper.orderToOrderEntity(order));
        return orderDataAccessMapper.orderEntityToOrder(savedOrder);
    }

    @Override
    public Optional<Order> findByTrackingId(TrackingId trackingId) {
        return orderJpaRepository.findByTrackingId(trackingId.getValue())
                .map(orderDataAccessMapper::orderEntityToOrder);
    }
}
