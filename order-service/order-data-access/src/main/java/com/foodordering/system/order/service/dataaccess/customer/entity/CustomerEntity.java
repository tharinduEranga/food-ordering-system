package com.foodordering.system.order.service.dataaccess.customer.entity;

import lombok.*;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "order_customer_m_view", schema = "customer")
@Entity
public class CustomerEntity {

    @Id
    private UUID id;

}
