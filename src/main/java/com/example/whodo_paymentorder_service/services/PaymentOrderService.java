package com.example.whodo_paymentorder_service.services;


import com.example.whodo_paymentorder_service.clients.WorkOrderServiceClient;
import com.example.whodo_paymentorder_service.models.Constants;
import com.example.whodo_paymentorder_service.models.DateUtcOffset;
import com.example.whodo_paymentorder_service.models.PaymentOrder;
import com.example.whodo_paymentorder_service.models.WorkOrder;
import com.example.whodo_paymentorder_service.repositories.PaymentOrderRepository;
import com.mercadopago.resources.merchantorder.MerchantOrder;
import com.mercadopago.resources.merchantorder.MerchantOrderPayment;
import com.mercadopago.resources.payment.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class PaymentOrderService {

    private final PaymentOrderRepository repository;
    private final WorkOrderServiceClient mWorkOrderServiceClient;
    private static final Logger log = LoggerFactory.getLogger(PaymentOrderService.class);

    public PaymentOrderService(PaymentOrderRepository repository, WorkOrderServiceClient mWorkOrderServiceClient) {
        this.repository = repository;
        this.mWorkOrderServiceClient = mWorkOrderServiceClient;
    }

    public Optional<PaymentOrder> getById(String id) {
        return repository.findById(id);
    }
    // Registrar nueva PaymentOrder
    public PaymentOrder createPaymentOrder(PaymentOrder order) {
        order.setCreationDate(order.getCreationDate());
        order.setLastUpdated(order.getCreationDate());
        order.setState(Constants.CREATED);
        return repository.save(order);
    }
    // Actualizar PaymentOrder por ID
    public Optional<PaymentOrder> updatePaymentOrder(PaymentOrder updated) {
        return repository.findById(updated.getId()).map(existing -> {
            // Tomamos el offset de la WorkOrder (del creationDate ya guardado)
            String workOrderOffset = existing.getCreationDate().getTimeZoneOffset();
            ZoneOffset zoneOffset = ZoneOffset.of(workOrderOffset);

            // Generamos el nuevo lastUpdated con ese offset
            OffsetDateTime now = OffsetDateTime.now(zoneOffset);
            existing.setLastUpdated(new DateUtcOffset(now.toString()));

            // Actualizamos el resto de los campos
            if(updated.getProvider() != null){
                existing.setProvider(updated.getProvider());
            }
            if(updated.getPaymentDate() != null){
                existing.setPaymentDate(updated.getPaymentDate());
            }
            if (updated.getPreference() != null) {
                existing.setPreference(updated.getPreference());
            }
            if (updated.getState() != null) {
                existing.setState(updated.getState());
            }
            if (updated.getMerchantOrders() != null) {
                Map<String, PaymentOrder.MerchantOrder> merged = new LinkedHashMap<>();
                if (existing.getMerchantOrders() != null) {
                    existing.getMerchantOrders().forEach(mo -> merged.put(mo.getId(), mo));
                }
                updated.getMerchantOrders().forEach(mo -> merged.put(mo.getId(), mo));
                existing.setMerchantOrders(new ArrayList<>(merged.values()));
            }
            return repository.save(existing);
        });
    }
    // Actualizar estado de la orden según pagos
    public Optional<PaymentOrder> reconcilePaymentOrder(String id) {
        return repository.findById(id).map(order -> {
            try {
                boolean anyApproved = order.getMerchantOrders().stream()
                        .flatMap(mo -> mo.getPayments().stream())
                        .anyMatch(p -> "approved".equalsIgnoreCase(p.getStatus()));

                boolean allRejected = order.getMerchantOrders().stream()
                        .flatMap(mo -> mo.getPayments().stream())
                        .allMatch(p -> "rejected".equalsIgnoreCase(p.getStatus()));

                boolean anyRefunded = order.getMerchantOrders().stream()
                        .flatMap(mo -> mo.getPayments().stream())
                        .anyMatch(p ->
                                "refunded".equalsIgnoreCase(p.getStatus()) ||
                                        "partially_refunded".equalsIgnoreCase(p.getStatus()) ||
                                        "charged_back".equalsIgnoreCase(p.getStatus())
                        );

                if (anyRefunded) {
                    order.setState(Constants.CLOSED_REFUNDED);
                } else if (anyApproved) {
                    order.setState(Constants.CLOSED_APPROVED);
                    order.setPaymentDate(new DateUtcOffset(OffsetDateTime.now().toString()));
                } else if (allRejected) {
                    order.setState(Constants.CLOSED_REJECTED);
                } else {
                    order.setState(Constants.PENDING);
                }

                order.setLastUpdated(new DateUtcOffset(OffsetDateTime.now().toString()));

                // Guardar PaymentOrder
                PaymentOrder saved = repository.save(order);

                if (anyApproved){
                    // Si el save fue exitoso, actualizar la WorkOrder
                    WorkOrder WO = WorkOrder.builder()
                            .orderId(saved.getWorkOrderId())
                            .state(Constants.CONFIRMED)
                            .stateChangeDate(OffsetDateTime.now().toString())
                            .build();

                    mWorkOrderServiceClient.updateWorkOrderState(WO);
                }

                return saved;

            } catch (Exception e) {
                // Loguear el error y no actualizar la WorkOrder
                log.error("Error al reconciliar PaymentOrder con id {}: {}", id, e.getMessage(), e);
                return null;
            }
        });
    }

    // Cerrar PaymentOrder
    public Optional<PaymentOrder> closePaymentOrder(String paymentOrderId) {
        return repository.findById(paymentOrderId).map(order -> {
            if(Objects.equals(order.getReason(), Constants.INSPECTION_PAYMENT))
                order.setState(Constants.INSPECTION_REJECTED);
            else if(Objects.equals(order.getReason(), Constants.WORK_PAYMENT))
                order.setState(Constants.WORK_REJECTED);
            order.setLastUpdated(new DateUtcOffset(OffsetDateTime.now().toString()));
            return repository.save(order);
        });
    }

}
