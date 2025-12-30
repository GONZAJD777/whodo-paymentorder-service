package com.example.whodo_paymentorder_service.controllers;

import com.example.whodo_paymentorder_service.services.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/webhook/payment")
public class PaymentWebhookController {

    @Autowired
    private PaymentService paymentService;
    private static final Logger log = LoggerFactory.getLogger(PaymentWebhookController.class);

    @PostMapping("/success")
    public ResponseEntity<String> handleSuccess(@RequestBody Map<String, Object> payload)  {
        try {
            log.info("Webhook recibido: {}", payload);
            // Diferenciar por tipo de evento
            String topic = (String) payload.get("topic");
            String type = (String) payload.get("type");
            String action = (String) payload.get("action");

            if ("payment.created".equalsIgnoreCase(action)) {
//                // Caso 1: creación de pago
//                Map<String, Object> data = (Map<String, Object>) payload.get("data");
//                if (data != null && data.get("id") != null) {
//                    String paymentId = data.get("id").toString();
//                    log.info("Evento payment.created con ID: {}", paymentId);
//                    paymentService.registerPaymentCreated(paymentId);
//                }
            } else if ("payment".equalsIgnoreCase(topic)) {
//                // Caso 2: actualización de pago
//                String paymentId = (String) payload.get("resource");
//                log.info("Evento payment update con ID: {}", paymentId);
//                paymentService.validatePayment("mercadoPagoBridge",paymentId);
            } else if ("merchant_order".equalsIgnoreCase(topic)) {
                // Caso 3: actualización de orden
                String resource = (String) payload.get("resource");
                String orderId = resource.substring(resource.lastIndexOf("/") + 1);
                log.info("Evento merchant_order con ID: {}", orderId);
                paymentService.validateMerchantOrder("mercadoPagoBridge",orderId);
            }

            return ResponseEntity.ok("Webhook procesado");

            //boolean result = paymentService.validatePayment("mercadoPagoBridge",payload);
            // Aquí procesás la lógica de pago exitoso

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

}
