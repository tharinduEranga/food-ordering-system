package com.foodordering.system.payment.service.domain.ports.output.repository;

import com.foodordering.system.domain.valueobject.CustomerId;
import com.foodordering.system.payment.service.domain.entity.CreditHistory;

import java.util.List;
import java.util.Optional;

public interface CreditHistoryRepository {
    CreditHistory save(CreditHistory creditEntry);

    Optional<List<CreditHistory>> findByCustomerId(CustomerId customerId);
}
