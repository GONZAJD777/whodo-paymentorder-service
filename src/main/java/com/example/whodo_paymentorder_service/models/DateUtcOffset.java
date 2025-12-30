package com.example.whodo_paymentorder_service.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateUtcOffset {
    @Field("dateUtc")
    private Date dateUtc;

    @Field("timeZoneOffset")
    private String timeZoneOffset;

    @JsonCreator
    public DateUtcOffset(String isoDate) {
        OffsetDateTime odt = OffsetDateTime.parse(isoDate);
        this.dateUtc = Date.from(odt.toInstant());
        this.timeZoneOffset = odt.getOffset().toString();
    }
}
