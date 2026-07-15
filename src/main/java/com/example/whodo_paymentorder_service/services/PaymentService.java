package com.example.whodo_paymentorder_service.services;

import com.example.whodo_paymentorder_service.adapters.payment.PaymentBridge;
import com.example.whodo_paymentorder_service.models.PaymentOrder;
import com.example.whodo_paymentorder_service.models.preferencesDTO.PaymentRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mercadopago.resources.merchantorder.MerchantOrder;
import com.mercadopago.resources.merchantorder.MerchantOrderPayment;
import com.mercadopago.resources.preference.Preference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final ObjectMapper objectMapper;
    private final PaymentOrderService paymentOrderService;
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final Map<String, PaymentBridge> bridges;

    @Autowired
    public PaymentService(ObjectMapper objectMapper, List<PaymentBridge> bridgeList, PaymentOrderService paymentOrderService) {
        this.objectMapper = objectMapper;
        this.paymentOrderService = paymentOrderService;
        // Usa el nombre del @Component como clave (ej. "mercadoPagoBridge")
        this.bridges = bridgeList.stream()
                .collect(Collectors.toMap(
                        b -> b.getClass().getAnnotation(org.springframework.stereotype.Component.class).value(),
                        b -> b
                ));
    }
    public String createPayment(String paymentProvider, PaymentRequest request) {
        PaymentBridge bridge = bridges.get(paymentProvider);
        if (bridge == null) {
            throw new IllegalArgumentException("Payment Provider doesn´t exist: " + paymentProvider);
        }
        PaymentOrder order = paymentOrderService.getById(request.getExternalReference()).orElseThrow(() ->
                new IllegalArgumentException("PaymentOrder con ID " + request.getExternalReference() + " no encontrado."));
        String id = order.getId();

        try {
            if(order.getPreference() == null || order.getPreference().getSnapshot() == null){

                String preferenceJson = bridge.createPayment(request);
                Preference preference = objectMapper.readValue(preferenceJson, Preference.class);
                updatePaymentOrder(paymentProvider, preference, preferenceJson);
                log.warn("Se creo la preferencia {}.", preference.getInitPoint());
                return preference.getInitPoint();
            }

            if(order.getMerchantOrders() !=null && !order.getMerchantOrders().isEmpty()){
                MerchantOrder mMerchantOrder = validateMerchantOrder(order.getProvider(), order.getMerchantOrders().getFirst().getId(), "manual");
                if (!shouldShowPaymentUrl(mMerchantOrder)) {
                    log.info("PaymentOrder con id {} tiene un pago en proceso.", id);
                    throw new IllegalArgumentException("no se puede generar URL de pago: ya existe un pago en proceso.");
                }
            }
            log.warn("La preferencia no tiene intentos de pago se retorna para intentar nuevamente {}.", order.getPreference().getSnapshot().get("init_point").toString());

            return order.getPreference().getSnapshot().get("init_point").toString();
        } catch (Exception e) {
            log.error("Error al validar PaymentOrder con id {}: {}", id, e.getMessage(), e);
            throw new IllegalArgumentException("Error al validar PaymentOrder con id " + id + ": " + e.getMessage(), e);
        }
    }
    public void deletePayment(String paymentProvider, String paymentId) throws Exception {
        log.info("Cancelando Payment con ID: {}", paymentId);

        PaymentBridge bridge = bridges.get(paymentProvider);
        if (bridge == null) {
            throw new IllegalArgumentException("Payment Provider doesn´t exist: " + paymentProvider);
        }
        bridge.deletePayment(paymentId);
    }
    public MerchantOrder validateMerchantOrder(String paymentProvider, String orderId, String requestOrigin) throws JsonProcessingException {
        log.info("Validando Merchant Order con ID: {}", orderId);
        PaymentBridge bridge = bridges.get(paymentProvider);
        if (bridge == null) {
            throw new IllegalArgumentException("Payment Provider doesn´t exist: " + paymentProvider);
        }

        String merchantOrderJson = bridge.validateMerchantOrder(orderId);
        MerchantOrder merchantOrder = objectMapper.readValue(merchantOrderJson, MerchantOrder.class);

        if (Objects.equals(requestOrigin, "webhook")) {
            log.info("Webhook Merchant Order recibido: {}", merchantOrderJson);
            updatePaymentOrder(merchantOrder, paymentProvider, merchantOrderJson);
        } else {
            log.info("Validacion Manual de Merchant Order recibido: {}", merchantOrderJson);
        }
        return merchantOrder;
    }
    public void validatePayment(String paymentProvider, String paymentId) throws Exception {

        log.info("Validando ESTADO de PaymentID: {}", paymentId);

        PaymentBridge bridge = bridges.get(paymentProvider);
        if (bridge == null) {
            throw new IllegalArgumentException("Payment Provider doesn´t exist: " + paymentProvider);
        }

        bridge.validatePayment(paymentId);
    }
    public void updatePaymentOrder( String paymentProvider, Preference preference, String preferenceJson) throws JsonProcessingException{
        // 2. Parsear respuesta
        String preferenceId = preference.getId();
        String externalReference = preference.getExternalReference();

        // 3. Actualizar PaymentOrder
        PaymentOrder updated = PaymentOrder.builder()
                .id(externalReference)
                .provider(paymentProvider)
                .preference(new PaymentOrder.Preference(
                        preferenceId,
                        objectMapper.readValue(preferenceJson, new TypeReference<>() {})
                )).build();

        paymentOrderService.updatePaymentOrder(updated);
    }
    public void updatePaymentOrder(MerchantOrder pMerchantOrder, String paymentProvider, String merchantOrderJson) throws JsonProcessingException {
        PaymentOrder updated = PaymentOrder.builder()
                .id(pMerchantOrder.getExternalReference())
                .provider(paymentProvider)
                .merchantOrders(List.of(
                        new PaymentOrder.MerchantOrder(
                                pMerchantOrder.getId().toString(),
                                pMerchantOrder.getStatus(),
                                objectMapper.readValue(merchantOrderJson, new TypeReference<>() {}),
                                pMerchantOrder.getPayments().stream()
                                        .map(p -> new PaymentOrder.PaymentAttempt(
                                                p.getId().toString(),
                                                p.getStatus(),
                                                p.getTransactionAmount(),
                                                p.getDateCreated().toString()
                                        ))
                                        .collect(Collectors.toList())
                        )
                )).build();

        String mUpdatedId = paymentOrderService.updatePaymentOrder(updated).isPresent() ? updated.getId() : null;
        paymentOrderService.reconcilePaymentOrder(mUpdatedId);
    }
    boolean shouldShowPaymentUrl(MerchantOrder order) {
        for (MerchantOrderPayment p : order.getPayments()) {
            switch (p.getStatus().toLowerCase()) {
                case "approved":
                case "pending":
                case "in_process":
                    return false; // ya está pagado o en validación
                case "rejected":
                case "cancelled":
                case "refunded":
                case "charged_back":
                    break;
            }
        }
        return true;
    }
    public void registerPaymentCreated(String paymentId) {
        log.info("Se creo el PaymentOrder con el paymentdId: {}", paymentId);
    }
}