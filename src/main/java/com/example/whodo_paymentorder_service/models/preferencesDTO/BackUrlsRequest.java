package com.example.whodo_paymentorder_service.models.preferencesDTO;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BackUrlsRequest {
    private String success;
    private String pending;
    private String failure;
}

