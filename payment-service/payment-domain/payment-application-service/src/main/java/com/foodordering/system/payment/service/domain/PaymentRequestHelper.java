package com.foodordering.system.payment.service.domain;

import com.foodordering.system.domain.event.publisher.DomainEventPublisher;
import com.foodordering.system.domain.valueobject.CustomerId;
import com.foodordering.system.payment.service.domain.dto.PaymentRequest;
import com.foodordering.system.payment.service.domain.entity.CreditEntry;
import com.foodordering.system.payment.service.domain.entity.CreditHistory;
import com.foodordering.system.payment.service.domain.entity.Payment;
import com.foodordering.system.payment.service.domain.event.PaymentCancelledEvent;
import com.foodordering.system.payment.service.domain.event.PaymentCompletedEvent;
import com.foodordering.system.payment.service.domain.event.PaymentEvent;
import com.foodordering.system.payment.service.domain.event.PaymentFailedEvent;
import com.foodordering.system.payment.service.domain.exception.PaymentApplicationServiceException;
import com.foodordering.system.payment.service.domain.function.PaymentEventExecutor;
import com.foodordering.system.payment.service.domain.mapper.PaymentDataMapper;
import com.foodordering.system.payment.service.domain.ports.output.repository.CreditEntryRepository;
import com.foodordering.system.payment.service.domain.ports.output.repository.CreditHistoryRepository;
import com.foodordering.system.payment.service.domain.ports.output.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentRequestHelper {
    private final PaymentDomainService paymentDomainService;
    private final PaymentDataMapper paymentDataMapper;
    private final PaymentRepository paymentRepository;
    private final CreditEntryRepository creditEntryRepository;
    private final CreditHistoryRepository creditHistoryRepository;
    private final DomainEventPublisher<PaymentCompletedEvent> paymentCompletedEventDomainEventPublisher;
    private final DomainEventPublisher<PaymentCancelledEvent> paymentCancelledEventDomainEventPublisher;
    private final DomainEventPublisher<PaymentFailedEvent> paymentFailedEventDomainEventPublisher;


    @Transactional
    public PaymentEvent persistPayment(PaymentRequest paymentRequest) {
        log.info("Received payment complete event for order id: {}", paymentRequest.getOrderId());
        return executePaymentEvent(paymentDataMapper.paymentRequestToPayment(paymentRequest),
                (payment, creditEntry, creditHistories, failureMessages) -> paymentDomainService
                        .validateAndInitiatePayment(payment, creditEntry, creditHistories, failureMessages,
                                paymentCompletedEventDomainEventPublisher, paymentFailedEventDomainEventPublisher));
    }

    @Transactional
    public PaymentEvent persistCancelPayment(PaymentRequest paymentRequest) {
        log.info("Received payment cancel event for order id: {}", paymentRequest.getOrderId());
        Optional<Payment> paymentOptional = paymentRepository
                .findByOrderId(UUID.fromString(paymentRequest.getOrderId()));
        if (paymentOptional.isEmpty()) {
            log.error("Could not find the payment for the order id: {}", paymentRequest.getOrderId());
            throw new PaymentApplicationServiceException("Could not find the payment for the order id: " +
                    paymentRequest.getOrderId());
        }
        return executePaymentEvent(paymentOptional.get(), (payment, creditEntry, creditHistories, failureMessages) ->
                paymentDomainService.validateAndCancelPayment(payment, creditEntry, creditHistories, failureMessages,
                        paymentCancelledEventDomainEventPublisher, paymentFailedEventDomainEventPublisher));
    }


    private CreditEntry getCreditEntry(CustomerId customerId) {
        Optional<CreditEntry> creditEntryOptional = creditEntryRepository.findByCustomerId(customerId);
        if (creditEntryOptional.isEmpty()) {
            log.error("Credit entry is not found for the customer: {}", customerId.getValue());
            throw new PaymentApplicationServiceException("Credit entry is not found for the customer: " +
                    customerId.getValue());
        }
        return creditEntryOptional.get();
    }

    private List<CreditHistory> getCreditHistory(CustomerId customerId) {
        Optional<List<CreditHistory>> creditHistoryOptional = creditHistoryRepository.findByCustomerId(customerId);
        if (creditHistoryOptional.isEmpty()) {
            log.error("Credit histories are not found for the customer: {}", customerId.getValue());
            throw new PaymentApplicationServiceException("Credit histories are not found for the customer: " +
                    customerId.getValue());
        }
        return creditHistoryOptional.get();
    }

    private PaymentEvent executePaymentEvent(Payment payment, PaymentEventExecutor paymentEventExecutor) {
        CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
        List<CreditHistory> creditHistories = getCreditHistory(payment.getCustomerId());
        List<String> failureMessages = new ArrayList<>();
        PaymentEvent paymentEvent = paymentEventExecutor
                .execute(payment, creditEntry, creditHistories, failureMessages);
        persistDbObjects(payment, creditEntry, creditHistories, failureMessages);
        return paymentEvent;
    }

    private void persistDbObjects(Payment payment, CreditEntry creditEntry,
                                  List<CreditHistory> creditHistories, List<String> failureMessages) {
        paymentRepository.save(payment);
        if (failureMessages.isEmpty()) {
            creditEntryRepository.save(creditEntry);
            creditHistoryRepository.save(creditHistories.get(creditHistories.size() - 1));
        }
    }

}
