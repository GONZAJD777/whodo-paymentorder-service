package com.example.whodo_paymentorder_service.repositories;

import com.example.whodo_paymentorder_service.models.PaymentOrder;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentOrderRepository extends MongoRepository<PaymentOrder, String> {
}
