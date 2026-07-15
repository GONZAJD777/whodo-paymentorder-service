package com.example.whodo_paymentorder_service.clients;

import com.example.whodo_paymentorder_service.models.preferencesDTO.PaymentRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import static com.example.whodo_paymentorder_service.models.Constants.HEADER_KEY_AUTHORIZATION;

@Service
public class MercadoPagoClient {

    private final RestClient restClient;
    @Autowired
    private ObjectMapper objectMapper;

    public MercadoPagoClient(@Qualifier("mercadoPagoRestClient") RestClient restClient) {
        this.restClient = restClient;
    }
    // Crear preferencia
    public ResponseEntity<String> preference(String accessToken, PaymentRequestDTO payload) {
        return restClient.post()
                .uri("/checkout/preferences")
                .header(HEADER_KEY_AUTHORIZATION, "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .body(payload)
                .retrieve()
                .toEntity(String.class);
    }
    // Consultar merchant order por ID
    public ResponseEntity<String> getMerchantOrder(String accessToken, String orderId) {
        return restClient.get()
                .uri("/merchant_orders/" + orderId)
                .header(HEADER_KEY_AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .toEntity(String.class);
    }

    // Consultar pago por ID
    public ResponseEntity<String> getPayment(String accessToken, String paymentId) {
        return restClient.get()
                .uri("/v1/payments/" + paymentId)
                .header(HEADER_KEY_AUTHORIZATION, "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .retrieve()
                .toEntity(String.class);
    }

    public ResponseEntity<String> cancelPayment(String accessToken, String paymentURL) {
        return restClient.post()
                .uri("/v1/payments/" + paymentURL +"/cancel")
                .header(HEADER_KEY_AUTHORIZATION, "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .retrieve()
                .toEntity(String.class);
    }

}
