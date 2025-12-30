package com.example.whodo_paymentorder_service.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "payment_orders")
public class PaymentOrder {

    @Id
    private String id;                  // PK interna (se mapea a _id en Mongo)
    @Field("work_order_id")
    private String workOrderId;         // Relación con tu WorkOrder
    @Field("payer_id")
    private String payerId;             // Usuario que paga
    @Field("payee_id")
    private String payeeId;             // Usuario que recibe
    private String reason;              // Motivo del pago
    private BigDecimal amount;          // Monto esperado
    private String state; // Enum con estados
    private String provider;            // MERCADO_PAGO, STRIPE, etc.
    @Field("creation_date")
    private DateUtcOffset creationDate;
    @Field("last_updated")
    private DateUtcOffset lastUpdated;
    @Field("payment_date")
    private DateUtcOffset paymentDate;
    private Preference preference;      // Subdocumento con info de la preferencia
    private List<MerchantOrder> merchantOrders; // Lista de órdenes asociadas

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Preference {
        private String id;
        private Map<String, Object> snapshot;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MerchantOrder {
        private String id;
        private String status;
        private Map<String, Object> snapshot;
        private List<PaymentAttempt> payments;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentAttempt {
        private String id;
        private String status;
        private BigDecimal transactionAmount;
        private String dateCreated;
    }
}
