package com.example.whodo_paymentorder_service.controllers;

import com.example.whodo_paymentorder_service.models.preferencesDTO.PaymentRequest;
import com.example.whodo_paymentorder_service.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/createPayment")
    public ResponseEntity<Map<String, String>> createPayment(@RequestBody PaymentRequest request,@RequestParam String paymentProvider) {
        try {
            String url = paymentService.createPayment(paymentProvider, request);
            return ResponseEntity.ok(Map.of("url", url));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
    @PostMapping("/cancelPayment")
    public ResponseEntity<Map<String, String>> deletePayment(@RequestParam String paymentId ,@RequestParam String paymentProvider ) {
        try {
            paymentService.deletePayment(paymentProvider, paymentId);
            return ResponseEntity.ok(Map.of("status", "Payment cancelled successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}
