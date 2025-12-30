package com.example.whodo_paymentorder_service.models;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {
    @Field("latitude")
    private Double latitude;

    @Field("longitude")
    private Double longitude;

    @Field("geohash")
    private String geohash;

}