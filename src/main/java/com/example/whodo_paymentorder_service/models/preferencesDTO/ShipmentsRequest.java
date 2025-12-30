package com.example.whodo_paymentorder_service.models.preferencesDTO;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@Getter
@Setter
public class ShipmentsRequest {
    private Boolean localPickup;
    private String dimensions;
    private Long defaultShippingMethod;
    private List<FreeMethodRequest> freeMethods;
    private BigDecimal cost;
    private Boolean freeShipping;
    private ReceiverAddressRequest receiverAddress;
    private String mode;
}
