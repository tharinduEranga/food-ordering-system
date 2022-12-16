package com.foodordering.system.order.service.domain;

import com.foodordering.system.order.service.domain.dto.track.TrackOrderQuery;
import com.foodordering.system.order.service.domain.dto.track.TrackOrderResponse;
import com.foodordering.system.order.service.domain.entity.Order;
import com.foodordering.system.order.service.domain.exception.OrderNotFoundException;
import com.foodordering.system.order.service.domain.mapper.OrderDataMapper;
import com.foodordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.foodordering.system.order.service.domain.valueobject.TrackingId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderTrackCommandHandler {

    private final OrderDataMapper orderDataMapper;

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public TrackOrderResponse createOrder(TrackOrderQuery trackOrderQuery) {
        Optional<Order> optionalOrder = orderRepository.findByTrackingId(new TrackingId(trackOrderQuery.getOrderTrackingId()));
        if (optionalOrder.isEmpty()) {
            log.warn("Could not find order with tracking id: {}", trackOrderQuery.getOrderTrackingId());
            throw new OrderNotFoundException("Could not find order with tracking id "
                    + trackOrderQuery.getOrderTrackingId());
        }
        return orderDataMapper.orderToTrackOrderResponse(optionalOrder.get());
    }
}
