package com.example.whodo_paymentorder_service.services;

import com.example.whodo_paymentorder_service.adapters.payment.PaymentBridge;
import com.example.whodo_paymentorder_service.models.PaymentOrder;
import com.example.whodo_paymentorder_service.models.preferencesDTO.PaymentRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mercadopago.resources.merchantorder.MerchantOrder;
import com.mercadopago.resources.preference.Preference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
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
    public String createPayment(String paymentProvider, PaymentRequest request) throws Exception {
        PaymentBridge bridge = bridges.get(paymentProvider);
        if (bridge == null) {
            throw new IllegalArgumentException("Payment Provider doesn´t exist: " + paymentProvider);
        }

        // 1. Crear preferencia en MercadoPago
        String preferenceJson = bridge.createPayment(request);

        Preference preference = objectMapper.readValue(preferenceJson, Preference.class);
        // 2. Parsear respuesta
        String preferenceId = preference.getId();
        String initPoint = preference.getInitPoint();
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

        // 4. Retornar al frontend la URL de pago
        return initPoint;
    }
    public void validateMerchantOrder(String paymentProvider, String orderId) throws JsonProcessingException {
        log.info("Validando Merchant Order con ID: {}", orderId);

        PaymentBridge bridge = bridges.get(paymentProvider);
        if (bridge == null) {
            throw new IllegalArgumentException("Payment Provider doesn´t exist: " + paymentProvider);
        }

        String merchantOrderJson = bridge.validateMerchantOrder(orderId);

        MerchantOrder merchantOrder = objectMapper.readValue(merchantOrderJson, MerchantOrder.class);

        PaymentOrder updated = PaymentOrder.builder()
                    .id(merchantOrder.getExternalReference())
                    .provider(paymentProvider)
                    .merchantOrders(List.of(
                            new PaymentOrder.MerchantOrder(
                                    merchantOrder.getId().toString(),
                                    merchantOrder.getStatus(),
                                    objectMapper.readValue(merchantOrderJson, new TypeReference<>() {}),
                                    merchantOrder.getPayments().stream()
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
    public void validatePayment(String paymentProvider, String paymentId) throws Exception {

        log.info("Validando ESTADO de PaymentID: {}", paymentId);

        PaymentBridge bridge = bridges.get(paymentProvider);
        if (bridge == null) {
            throw new IllegalArgumentException("Payment Provider doesn´t exist: " + paymentProvider);
        }

        bridge.validatePayment(paymentId);
    }
    public void registerPaymentCreated(String paymentId) {
        log.info("Se creo el PaymentOrder con el paymentdId: {}", paymentId);
    }
}