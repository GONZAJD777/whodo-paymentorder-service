package com.example.whodo_paymentorder_service.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data                   // Genera getters, setters, toString, equals, hashCode
@AllArgsConstructor     // Genera constructor con todos los campos
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}

