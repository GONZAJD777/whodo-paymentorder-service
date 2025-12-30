package com.example.whodo_paymentorder_service.adapters;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;

public class TypeAdapter {
    public static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(OffsetDateTime.class, new JsonSerializer<OffsetDateTime>() {
                @Override
                public JsonElement serialize(OffsetDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                    return new JsonPrimitive(src.toString());
                }
            })
            .create();

}
