package com.example.whodo_paymentorder_service.models.preferencesDTO;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayerRequest {
    private String name;
    private String surname;
    private String email;
    private PhoneRequest phone;
    private IdentificationRequest identification;
    private AddressRequest address;
    private String dateCreated;
}
