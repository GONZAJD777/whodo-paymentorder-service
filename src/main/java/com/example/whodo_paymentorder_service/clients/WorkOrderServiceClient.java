package com.example.whodo_paymentorder_service.clients;

import com.example.whodo_paymentorder_service.models.WorkOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WorkOrderServiceClient {

    private final RestClient restClient;
    @Autowired
    private ObjectMapper objectMapper;

    public WorkOrderServiceClient(@Qualifier("workOrderServiceRestClient") RestClient restClient) {
        this.restClient = restClient;
    }
    // Crear preferencia
    public void updateWorkOrderState(WorkOrder payload) {
        restClient.put()
                .uri("/updateWorkOrder")
                .header("Content-Type", "application/json")
                .body(payload)
                .retrieve()
                .toEntity(String.class);
    }
}
