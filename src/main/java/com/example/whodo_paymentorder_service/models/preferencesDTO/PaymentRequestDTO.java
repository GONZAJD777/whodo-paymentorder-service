package com.example.whodo_paymentorder_service.models.preferencesDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaymentRequestDTO {

    @NotEmpty(message = "Debe haber al menos un item")
    @Valid
    @JsonProperty("items")
    private List<ItemRequest> items;

    @JsonProperty("back_urls")
    private BackUrlsRequest backUrls;

    @JsonProperty("notification_url")
    private String notificationUrl;

    // 🔎 Campos oficiales de MercadoPago
    @JsonProperty("external_reference")
    private String externalReference;

    @JsonProperty("metadata")
    private Map<String, Object> metadata;
}

