package com.foodordering.system.payment.service.domain.ports.output.repository;

import com.foodordering.system.domain.valueobject.CustomerId;
import com.foodordering.system.payment.service.domain.entity.CreditEntry;

import java.util.Optional;

public interface CreditEntryRepository {
    CreditEntry save(CreditEntry creditEntry);

    Optional<CreditEntry> findByCustomerId(CustomerId customerId);
}
