package com.example.whodo_paymentorder_service.models;

public class Constants {
    /// Constantes para Prometheus métricas del pool de conexiones HTTP
    public static final String METRIC_POOL_IDLE_NAME = "http.client.pool.idle.connections";
    public static final String METRIC_POOL_ACTIVE_NAME = "http.client.pool.active.connections";
    public static final String METRIC_POOL_PENDING_NAME = "http.client.pool.pending.connections";
    public static final String METRIC_POOL_MAX_NAME = "http.client.pool.max.connections";
    public static final String METRIC_POOL_NAME = "http_client_pool";
    public static final String METRIC_NAME = "whodo_paymentorder_service_client";
    public static final String METRIC_POOL_IDLE_DESC = "Número de conexiones inactivas en el pool de conexiones HTTP";
    public static final String METRIC_POOL_ACTIVE_DESC = "Número de conexiones activas en el pool de conexiones HTTP";
    public static final String METRIC_POOL_PENDING_DESC = "Número de conexiones pendientes en el pool de conexiones HTTP";
    public static final String METRIC_POOL_MAX_DESC = "Número máximo de conexiones en el pool de conexiones HTTP";

    /// Constantes para headers de requests
    public static final String HEADER_KEY_SESSION_ID = "X-Session-Id";
    public static final String HEADER_KEY_COUNTRY = "X-Country";
    public static final String HEADER_KEY_AUTHORIZATION = "Authorization";

    /// Payment Reasons
    public static final String INSPECTION_PAYMENT = "INSPECTION_PAYMENT";
    public static final String WORK_PAYMENT = "WORK_PAYMENT";
    public static final String EXTRACTION_PAYMENT = "EXTRACTION_PAYMENT";

    /// Payment States
    public static final String CREATED = "CREATED";
    public static final String PENDING = "PENDING";
    public static final String CLOSED_APPROVED = "CLOSED_APPROVED";
    public static final String CLOSED_REJECTED = "CLOSED_REJECTED";
    public static final String CLOSED_REFUNDED = "CLOSED_REFUNDED";
    public static final String INSPECTION_REJECTED = "WORK_PAYMENT_REJECT_BY_CUSTOMER";
    public static final String WORK_REJECTED = "INSPECTION_PAYMENT_REJECTED_BY_CUSTOMER";


    /// WorkOrder States
    public static final String OPEN="OPEN";
    public static final String ONEVALUATION="ONEVALUATION";
    public static final String PLANNED="PLANNED";
    public static final String CONFIRMED="CONFIRMED";
    public static final String DIAGNOSED="DIAGNOSED";
    public static final String ONPROGRESS="ONPROGRESS";
    public static final String DONE="DONE";
    public static final String CLOSED_WARRANTY="CLOSED_WARRANTY";


}
