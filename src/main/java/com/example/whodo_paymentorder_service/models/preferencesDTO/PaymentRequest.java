package com.example.whodo_paymentorder_service.models.preferencesDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaymentRequest {

    @NotEmpty(message = "Debe haber al menos un item")
    @Valid
    @JsonProperty("items")
    private List<ItemRequest> items;

    @JsonProperty("payer")
    private PayerRequest payer;

    @JsonProperty("back_urls")
    private BackUrlsRequest backUrls;

    @JsonProperty("payment_methods")
    private PaymentMethodsRequest paymentMethods;

    @JsonProperty("shipments")
    private ShipmentsRequest shipments;

    @JsonProperty("notification_url")
    private String notificationUrl;

    @JsonProperty("additional_info")
    private String additionalInfo;

    @JsonProperty("auto_return")
    private String autoReturn;

    @JsonProperty("external_reference")
    private String externalReference;

    @JsonProperty("expires")
    private Boolean expires;

    @JsonProperty("expiration_date_from")
    private String expirationDateFrom;

    @JsonProperty("expiration_date_to")
    private String expirationDateTo;

    @JsonProperty("marketplace")
    private String marketplace;

    @JsonProperty("marketplace_fee")
    private BigDecimal marketplaceFee;

    @JsonProperty("differential_pricing")
    private DifferentialPricingRequest differentialPricing;

    @JsonProperty("operation_type")
    private String operationType;

    @JsonProperty("statement_descriptor")
    private String statementDescriptor;

    // @JsonProperty("tracks")
    // private List<TrackRequest> tracks;

    @JsonProperty("metadata")
    private Map<String, Object> metadata;
}
