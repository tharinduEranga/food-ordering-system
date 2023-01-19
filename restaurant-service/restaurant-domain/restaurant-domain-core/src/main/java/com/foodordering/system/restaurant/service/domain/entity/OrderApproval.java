package com.foodordering.system.restaurant.service.domain.entity;

import com.foodordering.system.domain.entity.BaseEntity;
import com.foodordering.system.domain.valueobject.OrderApprovalStatus;
import com.foodordering.system.domain.valueobject.OrderId;
import com.foodordering.system.domain.valueobject.RestaurantId;
import com.foodordering.system.restaurant.service.domain.valueobject.OrderApprovalId;

import java.util.UUID;

public class OrderApproval extends BaseEntity<OrderApprovalId> {
    private final RestaurantId restaurantId;
    private final OrderApprovalId orderApprovalId;
    private final OrderApprovalStatus approvalStatus;
    private final OrderId<UUID> orderId;

    private OrderApproval(Builder builder) {
        super.setId(builder.orderApprovalId);
        restaurantId = builder.restaurantId;
        orderApprovalId = builder.orderApprovalId;
        approvalStatus = builder.approvalStatus;
        orderId = builder.orderId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public RestaurantId getRestaurantId() {
        return restaurantId;
    }

    public OrderId<UUID> getOrderId() {
        return orderId;
    }

    public OrderApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public OrderApprovalId getOrderApprovalId() {
        return orderApprovalId;
    }

    public static final class Builder {
        private OrderApprovalId orderApprovalId;
        private RestaurantId restaurantId;
        private OrderApprovalStatus approvalStatus;
        private OrderId<UUID> orderId;

        private Builder() {
        }


        public Builder orderApprovalId(OrderApprovalId val) {
            orderApprovalId = val;
            return this;
        }

        public Builder restaurantId(RestaurantId val) {
            restaurantId = val;
            return this;
        }

        public Builder approvalStatus(OrderApprovalStatus val) {
            approvalStatus = val;
            return this;
        }

        public Builder orderId(OrderId<UUID> val) {
            orderId = val;
            return this;
        }

        public OrderApproval build() {
            return new OrderApproval(this);
        }
    }
}
