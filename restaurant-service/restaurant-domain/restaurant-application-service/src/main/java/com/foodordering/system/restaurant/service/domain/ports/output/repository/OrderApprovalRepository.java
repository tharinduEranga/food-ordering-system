package com.foodordering.system.restaurant.service.domain.ports.output.repository;

import com.foodordering.system.restaurant.service.domain.entity.OrderApproval;

public interface OrderApprovalRepository {
    OrderApproval save(OrderApproval orderApproval);
}
