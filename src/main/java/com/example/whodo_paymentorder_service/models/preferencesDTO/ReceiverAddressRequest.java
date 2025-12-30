package com.example.whodo_paymentorder_service.models.preferencesDTO;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class ReceiverAddressRequest {
    private String zipCode;
    private String streetName;
    private String cityName;
    private String stateName;
    private Integer streetNumber;
    private String countryName;
    private String floor;
    private String apartment;
}
