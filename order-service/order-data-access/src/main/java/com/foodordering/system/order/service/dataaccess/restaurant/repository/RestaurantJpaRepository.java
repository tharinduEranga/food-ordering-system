package com.foodordering.system.order.service.dataaccess.restaurant.repository;

import com.foodordering.system.order.service.dataaccess.restaurant.entity.RestaurantEntity;
import com.foodordering.system.order.service.dataaccess.restaurant.entity.RestaurantIdEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RestaurantJpaRepository extends JpaRepository<RestaurantEntity, RestaurantIdEntityId> {
    Optional<List<RestaurantEntity>> findByRestaurantIdAndProductIdIn(UUID restaurantId, List<UUID> productIds);
}
