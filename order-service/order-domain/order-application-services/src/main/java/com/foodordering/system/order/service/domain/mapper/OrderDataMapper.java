package com.foodordering.system.order.service.domain.mapper;

import com.foodordering.system.domain.valueobject.CustomerId;
import com.foodordering.system.domain.valueobject.Money;
import com.foodordering.system.domain.valueobject.ProductId;
import com.foodordering.system.domain.valueobject.RestaurantId;
import com.foodordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.foodordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.foodordering.system.order.service.domain.dto.create.OrderAddress;
import com.foodordering.system.order.service.domain.dto.track.TrackOrderResponse;
import com.foodordering.system.order.service.domain.entity.Order;
import com.foodordering.system.order.service.domain.entity.OrderItem;
import com.foodordering.system.order.service.domain.entity.Product;
import com.foodordering.system.order.service.domain.entity.Restaurant;
import com.foodordering.system.order.service.domain.valueobject.StreetAddress;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderDataMapper {

    public Restaurant createOrderCommand(CreateOrderCommand createOrderCommand) {
        return Restaurant.builder()
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .products(createOrderCommand.getItems()
                        .stream()
                        .map(orderItem -> new Product(new ProductId(orderItem.getProductId())))
                        .collect(Collectors.toList())
                )
                .build();
    }

    public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand) {
        return Order.builder()
                .customerId(new CustomerId(createOrderCommand.getCustomerId()))
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .deliveryAddress(orderAddressToStreetAddress(createOrderCommand.getAddress()))
                .price(new Money(createOrderCommand.getPrice()))
                .items(orderItemsToOrderItemEntities(createOrderCommand.getItems()))
                .build();
    }

    public CreateOrderResponse orderToCreateOrderResponse(Order orderResult, String message) {
        return CreateOrderResponse.builder()
                .orderTrackingId(orderResult.getTrackingId().getValue())
                .orderStatus(orderResult.getOrderStatus())
                .message(message)
                .build();
    }

    public TrackOrderResponse orderToTrackOrderResponse(Order order) {
        return TrackOrderResponse.builder()
                .orderTrackingId(order.getTrackingId().getValue())
                .orderStatus(order.getOrderStatus())
                .failureMessages(order.getFailureMessages())
                .build();
    }

    public Restaurant createOrderCommandToRestaurant(CreateOrderCommand createOrderCommand) {
        return Restaurant.builder()
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .active(true)
                .products(orderItemsToProducts(createOrderCommand.getItems()))
                .build();
    }

    private List<OrderItem> orderItemsToOrderItemEntities(List<com.foodordering.system.order.service.domain.dto.create.OrderItem> orderItems) {
        return orderItems.stream()
                .map(orderItem -> OrderItem.builder()
                        .product(new Product(new ProductId(orderItem.getProductId())))
                        .price(new Money(orderItem.getPrice()))
                        .qty(orderItem.getQuantity())
                        .subTotal(new Money(orderItem.getSubTotal()))
                        .build())
                .collect(Collectors.toList());
    }

    private List<Product> orderItemsToProducts(List<com.foodordering.system.order.service.domain.dto.create.OrderItem> orderItems) {
        return orderItems
                .stream()
                .map(orderItem -> new Product(new ProductId(orderItem.getProductId())))
                .collect(Collectors.toList());
    }

    private StreetAddress orderAddressToStreetAddress(OrderAddress address) {
        return new StreetAddress(
                UUID.randomUUID(),
                address.getStreet(),
                address.getPostalCode(),
                address.getCity()
        );
    }

}
