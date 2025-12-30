package com.example.whodo_paymentorder_service.models.preferencesDTO;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Builder
@Getter
@Setter
public class PaymentMethodsRequest {
    private String defaultPaymentMethodId;
    private List<ExcludedPaymentMethodRequest> excludedPaymentMethods;
    private List<ExcludedPaymentTypeRequest> excludedPaymentTypes;
    private Integer installments;
    private Integer defaultInstallments;
}

