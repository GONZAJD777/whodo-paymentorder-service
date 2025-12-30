package com.example.whodo_paymentorder_service.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "work_orders")
public class WorkOrder {
    @Id
    @Schema(description = "ID de la orden")
    private String orderId;
    @Schema(description = "Categoría del trabajo demandado")
    @Field("specialization")
    private String specialization;
    @Schema(description = "Descripción del trabajo a realizar por el cliente")
    @Field("description")
    private String description;
    @Schema(description = "Fecha y hora de creación de la orden en formato YYYYMMDD24HHMMSS")
    @Field("creationDate")
    private String creationDate;
    @Schema(description = "Fecha y hora hasta la cual la orden estará disponible para ser tomada por un proveedor")
    @Field("timeLimit")
    private String timeLimit;
    @Schema(description = "Fecha y hora del último cambio de estado en formato YYYYMMDD24HHMMSS")
    @Field("stateChangeDate")
    private String stateChangeDate;
    @Schema(description = "Estado de la orden")
    @Field("state")
    private String state;
    @Schema(description = "Información del cliente que demanda el trabajo")
    @Field("customer")
    private Customer customer;
    @Schema(description = "Información del proveedor que toma el trabajo")
    @Field("provider")
    private Provider provider;
    @Schema(description = "Información de la inspección del trabajo")
    @Field("inspection")
    private Inspection inspection;
    @Schema(description = "Información del trabajo realizado")
    @Field("work")
    private Work work;
    @Schema(description = "Puntuacion otorgada por el Cliente al Trabajador luego de finalizar el trabajo")
    @Field("feedback")
    private Feedback feedback;

    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Customer {

        @Schema(description = "ID del cliente")
        @Field("customerId")
        private String customerId;

        @Schema(description = "Nombre del cliente")
        @Field("customerName")
        private String customerName;

        @Schema(description = "Dirección del cliente")
        @Field("customerAddress")
        private String customerAddress;

        @Schema(description = "Ubicación del cliente")
        @Field("customerLocation")
        private Location customerLocation;

        @Schema(description = "Número de teléfono del cliente")
        @Field("customerPhone")
        private Phone customerPhone;
    }

    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Provider {

        @Schema(description = "ID del proveedor")
        @Field("providerId")
        private String providerId;

        @Schema(description = "Nombre del proveedor")
        @Field("providerName")
        private String providerName;

        @Schema(description = "Dirección del proveedor")
        @Field("providerAddress")
        private String providerAddress;

        @Schema(description = "Ubicación del proveedor")
        @Field("providerLocation")
        private Location providerLocation;

        @Schema(description = "Número de teléfono del proveedor")
        @Field("providerPhone")
        private Phone providerPhone;
    }

    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Inspection {

        @Schema(description = "Fecha y hora de cita de inspección en formato YYYYMMDD24HHMMSS")
        @Field("inspectionDate")
        private String inspectionDate;

        @Schema(description = "Cargo por la visita para inspección, es opcional")
        @Field("inspectionCharges")
        private Integer inspectionCharges;

        @Schema(description = "Fecha máxima para aceptar la cita de inspección del trabajo")
        @Field("inspectionTimeLimit")
        private String inspectionTimeLimit;

        @Schema(description = "ID de la orden de pago generada como registro del pago de la inspección")
        @Field("inspectionPaymentOrder")
        private String inspectionPaymentOrder;

        @Schema(description = "Tarifa de inspección")
        @Field("inspectionFee")
        private Integer inspectionFee;

        @Schema(description = "Cumplimiento de la inspección")
        @Field("inspectionFullfilment")
        private String inspectionFullfilment;

        @Schema(description = "Reprogramación de la inspección")
        @Field("inspectionRescheduled")
        private String inspectionRescheduled;
    }

    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Work {

        @Schema(description = "Fecha y hora límite para aceptar la propuesta de trabajo YYYYMMDD24HHMMSS")
        @Field("proposalTimeLimitDate")
        private String proposalTimeLimitDate;

        @Schema(description = "Fecha y hora de inicio de trabajo en formato YYYYMMDD24HHMMSS")
        @Field("workStartDate")
        private String workStartDate;

        @Schema(description = "Fecha y hora de fin de trabajo en formato YYYYMMDD24HHMMSS")
        @Field("workEndDate")
        private String workEndDate;

        @Schema(description = "Costo de mano de obra")
        @Field("workLaborCost")
        private Integer workLaborCost;

        @Schema(description = "Costo de materiales")
        @Field("workMaterialsCost")
        private Integer workMaterialsCost;

        @Schema(description = "Tarifa de trabajo")
        @Field("workFee")
        private Integer workFee;

        @Schema(description = "Bitácora de tareas y comentarios del proveedor")
        @Field("detail")
        private String detail;

        @Schema(description = "ID de la orden de pago del trabajo")
        @Field("workPaymentOrder")
        private String workPaymentOrder;

        @Schema(description = "Fecha y hora de fin de garantía en formato YYYYMMDD24HHMMSS")
        @Field("workWarrantyEndDate")
        private String workWarrantyEndDate;

        @Schema(description = "Extensión del tiempo límite de trabajo")
        @Field("workLimitTimeExtension")
        private Integer workLimitTimeExtension;

    }

    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Feedback {

        @Schema(description = "Puntuación de apariencia")
        @Field("appereanceScore")
        private Integer appereanceScore;

        @Schema(description = "Puntuación de limpieza")
        @Field("cleanlinessScore")
        private Integer cleanlinessScore;

        @Schema(description = "Puntuación de velocidad")
        @Field("speedScore")
        private Integer speedScore;

        @Schema(description = "Puntuación de calidad")
        @Field("qualityScore")
        private Integer qualityScore;

        @Schema(description = "Impresiones (reseña)")
        @Field("impressions")
        private String impressions;

    }
}


