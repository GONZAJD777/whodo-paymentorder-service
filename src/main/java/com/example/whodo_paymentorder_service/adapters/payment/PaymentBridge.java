package com.example.whodo_paymentorder_service.adapters.payment;

import com.example.whodo_paymentorder_service.models.preferencesDTO.PaymentRequest;

public interface PaymentBridge {
    String createPayment(PaymentRequest request) throws Exception;
    void validatePayment(String payload) throws Exception;
    void deletePayment(String request);
    String validateMerchantOrder(String orderId);
}
