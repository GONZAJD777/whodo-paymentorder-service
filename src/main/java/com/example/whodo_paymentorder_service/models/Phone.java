package com.example.whodo_paymentorder_service.models;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Phone {
    @Field("number")
    private String number;

    @Field("ccn")
    private String ccn;

}
