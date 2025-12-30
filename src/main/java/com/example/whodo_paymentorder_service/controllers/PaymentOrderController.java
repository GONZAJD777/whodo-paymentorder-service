package com.example.whodo_paymentorder_service.controllers;

import com.example.whodo_paymentorder_service.models.PaymentOrder;
import com.example.whodo_paymentorder_service.services.PaymentOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/payment-orders")
public class PaymentOrderController {

    private final PaymentOrderService service;

    public PaymentOrderController(PaymentOrderService service) {
        this.service = service;
    }

    // Crear nueva PaymentOrder
    @PostMapping("/createPaymentOrder")
    public ResponseEntity<PaymentOrder> create(@RequestBody PaymentOrder order) {
        PaymentOrder created = service.createPaymentOrder(order);
        return ResponseEntity.ok(created);
    }

    // Obtener PaymentOrder por ID
    @GetMapping("/{id}")
    public ResponseEntity<PaymentOrder> getById(@PathVariable String id) {
        Optional<PaymentOrder> order = service.getById(id);
        return order.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Actualizar PaymentOrder
    @PutMapping("/{id}")
    public ResponseEntity<PaymentOrder> update(@RequestBody PaymentOrder updated) {
        Optional<PaymentOrder> order = service.updatePaymentOrder(updated);
        return order.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Reconciliar estado de PaymentOrder
    @PostMapping("/{id}/reconcile")
    public ResponseEntity<PaymentOrder> reconcile(@PathVariable String id) {
        Optional<PaymentOrder> order = service.reconcilePaymentOrder(id);
        return order.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
