package com.example.whodo_paymentorder_service.models.preferencesDTO;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class ExcludedPaymentMethodRequest {
    private String id;
}
