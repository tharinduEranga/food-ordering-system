package com.foodordering.system.payment.service.domain.function;

import com.foodordering.system.payment.service.domain.entity.CreditEntry;
import com.foodordering.system.payment.service.domain.entity.CreditHistory;
import com.foodordering.system.payment.service.domain.entity.Payment;
import com.foodordering.system.payment.service.domain.event.PaymentEvent;

import java.util.List;

@FunctionalInterface
public interface PaymentEventExecutor {
    PaymentEvent execute(Payment payment,
                         CreditEntry creditEntry,
                         List<CreditHistory> creditHistories,
                         List<String> failureMessages);
}
