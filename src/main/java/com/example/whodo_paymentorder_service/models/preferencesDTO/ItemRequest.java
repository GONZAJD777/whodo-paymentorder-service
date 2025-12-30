package com.example.whodo_paymentorder_service.models.preferencesDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ItemRequest {
    @JsonProperty("id")
    private String id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("picture_url")
    private String pictureUrl;

    @JsonProperty("category_id")
    private String categoryId;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("currency_id")
    private String currencyId;

    @JsonProperty("unit_price")
    private java.math.BigDecimal unitPrice;
}
