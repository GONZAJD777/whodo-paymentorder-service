package com.example.whodo_paymentorder_service.models.preferencesDTO;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class AddressRequest {
    private String zipCode;
    private String streetName;
    private Integer streetNumber;
}

