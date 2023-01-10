package com.foodordering.system.payment.service.domain.entity;

import com.foodordering.system.domain.entity.BaseEntity;
import com.foodordering.system.domain.valueobject.CustomerId;
import com.foodordering.system.domain.valueobject.Money;
import com.foodordering.system.payment.service.domain.valueobject.CreditEntryId;

public class CreditEntry extends BaseEntity<CreditEntryId> {

    private final CustomerId customerId;
    private Money totalCreditAmount;

    private CreditEntry(Builder builder) {
        super.setId(builder.creditEntryId);
        customerId = builder.customerId;
        totalCreditAmount = builder.totalCreditAmount;
    }

    public void addCreditAmount(Money money) {
        totalCreditAmount = totalCreditAmount.add(money);
    }

    public void subtractCreditAmount(Money money) {
        totalCreditAmount = totalCreditAmount.subtract(money);
    }

    public static Builder builder() {
        return new Builder();
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public Money getTotalCreditAmount() {
        return totalCreditAmount;
    }

    public static final class Builder {
        private CreditEntryId creditEntryId;
        private CustomerId customerId;
        private Money totalCreditAmount;

        private Builder() {
        }

        public Builder creditEntryId(CreditEntryId val) {
            creditEntryId = val;
            return this;
        }

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public Builder totalCreditAmount(Money val) {
            totalCreditAmount = val;
            return this;
        }

        public CreditEntry build() {
            return new CreditEntry(this);
        }
    }
}
