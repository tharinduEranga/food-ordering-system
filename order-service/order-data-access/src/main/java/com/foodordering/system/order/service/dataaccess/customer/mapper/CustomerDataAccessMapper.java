package com.foodordering.system.order.service.dataaccess.customer.mapper;

import com.foodordering.system.domain.valueobject.CustomerId;
import com.foodordering.system.order.service.dataaccess.customer.entity.CustomerEntity;
import com.foodordering.system.order.service.domain.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerDataAccessMapper {

    public Customer customerEntityToCustomer(CustomerEntity customerEntity) {
        return new Customer(new CustomerId(customerEntity.getId()));
    }
}
