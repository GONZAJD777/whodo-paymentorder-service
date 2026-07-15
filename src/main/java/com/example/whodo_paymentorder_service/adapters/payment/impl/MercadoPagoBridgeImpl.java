package com.example.whodo_paymentorder_service.adapters.payment.impl;

import com.example.whodo_paymentorder_service.adapters.payment.PaymentBridge;
import com.example.whodo_paymentorder_service.clients.MercadoPagoClient;
import com.example.whodo_paymentorder_service.models.preferencesDTO.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.resources.preference.Preference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component("mercadoPagoBridge")
public class MercadoPagoBridgeImpl implements PaymentBridge {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MercadoPagoClient mercadoPagoClient;
    private final String mercadoPagoAccessToken;
    private Preference preference;
    private static final Logger log = LoggerFactory.getLogger(MercadoPagoBridgeImpl.class);

    public MercadoPagoBridgeImpl(@Value("${mercado.pago.access.token}") String mercadoPagoAccessToken) {
        this.mercadoPagoAccessToken = mercadoPagoAccessToken;
        MercadoPagoConfig.setAccessToken(mercadoPagoAccessToken);
    }
    @Override
    public String createPayment(PaymentRequest request) {
        PaymentRequestDTO paymentRequestDTO = new PaymentRequestDTO();
        List<ItemRequest> items = mapItems(request.getItems());
        paymentRequestDTO.setItems(items);
        paymentRequestDTO.setBackUrls(mapBackUrls(request));
        paymentRequestDTO.setNotificationUrl(request.getNotificationUrl());

        // 🔎 Trazabilidad: asociar PaymentOrder y WorkOrder
        paymentRequestDTO.setExternalReference(request.getExternalReference());

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("payment_order_id", request.getMetadata().get("payment_order_id"));
        metadata.put("work_order_id", request.getMetadata().get("work_order_id"));
        paymentRequestDTO.setMetadata(metadata);

        try {
            String json = objectMapper.writeValueAsString(paymentRequestDTO);

            log.info("Token actual en uso: {}", mercadoPagoAccessToken);
            log.info("Payload enviado a Mercado Pago: {}", json);

            ResponseEntity<String> response = mercadoPagoClient.preference(mercadoPagoAccessToken, paymentRequestDTO);

            String preferenceJson = response.getBody();

            log.info("Preferencia creada correctamente: {}", preferenceJson);
            return preferenceJson;

        } catch (JsonProcessingException e) {
            log.error("Error al procesar el JSON", e);
            throw new RuntimeException("Error al procesar el JSON de Mercado Pago", e);

        } catch (Exception e) {
            log.error("Error inesperado al crear la preferencia", e);
            throw new RuntimeException("Error inesperado al crear la preferencia", e);
        }
    }
    @Override
    public void deletePayment(String paymentURL) {
        try {
            mercadoPagoClient.cancelPayment(paymentURL, mercadoPagoAccessToken);
            log.error( "Preferencia  cancelada correctamente: {}", paymentURL);
        } catch (Exception e) {
            log.error("Error inesperado al cancelar la preferencia", e);
            throw new RuntimeException("Error inesperado al cancelar la preferencia", e);
        }
    }

    @Override
    public String validateMerchantOrder(String orderId) {
        try {
            log.info("*************** Iniciando validacion de Merchant Order {} ***************", orderId);
            ResponseEntity<String> response = mercadoPagoClient.getMerchantOrder(mercadoPagoAccessToken, orderId);

            if (response.getStatusCode().is2xxSuccessful()) {
                String merchantOrderJson = response.getBody();
                JsonNode json = objectMapper.readTree(response.getBody());

                String status = json.get("status").asText();
                log.info("Merchant Order {} estado: {}", orderId, status);
                log.info("Merchant Order {} : ", merchantOrderJson);

                return merchantOrderJson;
            } else {
                log.warn("Error consultando merchant order {}: {}", orderId, response.getStatusCode());
            }
            log.info("*************** Fin validacion de Merchant Order {} ***************", orderId);
        } catch (Exception e) {
            log.error("Error procesando merchant order {}: {}", orderId, e.getMessage(), e);
        }
        return orderId;
    }
    @Override
    public void validatePayment(String paymentId) {
        ResponseEntity<String> response = mercadoPagoClient.getPayment(mercadoPagoAccessToken, paymentId);
        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                JsonNode json = objectMapper.readTree(response.getBody());
                String status = json.get("status").asText();
                log.info("Estado del pago {}: {}", paymentId, status);

                switch (status.toLowerCase()) {
                    case "approved":
                        log.info("Pago aprobado, actualizar orden en DB");
                        // Actualizar orden como pagada
                        break;
                    case "pending":
                        log.info("Pago pendiente, mantener estado en espera");
                        // Mantener estado
                        break;
                    case "rejected":
                        log.info("Pago rechazado, marcar orden como fallida");
                        // Actualizar orden como rechazada
                        break;
                }
            } catch (Exception e) {
                log.error("Error parseando respuesta de pago {}: {}", paymentId, e.getMessage());
            }
        } else {
            log.error("Error obteniendo pago {}: Código {} : Mensaje {}", paymentId, response.getStatusCode(), response.getBody());
        }
    }


    //*****************************************************************//
    private List<ItemRequest> mapItems(List<ItemRequest> items) {
        return items.stream()
                .map(item -> new ItemRequest(
                        item.getId(),
                        item.getTitle(),
                        item.getDescription(),
                        item.getPictureUrl(),
                        item.getCategoryId(),
                        item.getQuantity(),
                        item.getCurrencyId(),
                        item.getUnitPrice()
                ))
                .toList();
    }
    private BackUrlsRequest mapBackUrls(PaymentRequest dto) {
        if (dto == null) return null;
        BackUrlsRequest backUrls = dto.getBackUrls();
        return BackUrlsRequest.builder()
                .success(backUrls.getSuccess())
                .failure(backUrls.getFailure())
                .pending(backUrls.getPending())
                .build();
    }

}