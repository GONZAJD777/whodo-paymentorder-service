
package com.example.whodo_paymentorder_service.config;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import static com.example.whodo_paymentorder_service.models.Constants.*;

@Configuration
public class RestClientConfig {

    private final Integer maxConnectionsPool;
    private final Integer maxConnectionsPerRoute;
    private final MeterRegistry registry;
    private static final Logger log = LoggerFactory.getLogger(RestClientConfig.class);


    public RestClientConfig(
            @Value("${rest.client.config.pool.maxConnectionsPool}") Integer maxConnectionsPool,
            @Value("${rest.client.config.pool.maxConnectionsPerRoute}") Integer maxConnectionsPerRoute,
            MeterRegistry registry) {
        this.maxConnectionsPool = maxConnectionsPool;
        this.maxConnectionsPerRoute = maxConnectionsPerRoute;
        this.registry = registry;
    }

    @Bean(name = "mercadoPagoRestClient")
    public RestClient mercadoPagoRestClient(
            @Value("${rest.client.url.MercadoPago}") String mercadoPagoServiceUrl,
            @Value("${rest.client.config.MercadoPago.connectionTimeout}") int mercadoPagoConnectionTimeout,
            @Value("${rest.client.config.MercadoPago.readTimeout}") int mercadoPagoReadTimeout,
            PoolingHttpClientConnectionManager manager) {

        return RestClient.builder()
                .baseUrl(mercadoPagoServiceUrl)
                .requestFactory(clientHttpRequestFactory(mercadoPagoConnectionTimeout, mercadoPagoReadTimeout, manager))
                .requestInterceptor((request, body, execution) -> {
                    log.debug("=== REQUEST DEBUG ===");
                    log.debug("URI: {}", request.getURI());
                    log.debug("Method: {}", request.getMethod());
                    log.debug("Headers: {}", request.getHeaders());
                    log.debug("Body (raw): {}", new String(body));
                    return execution.execute(request, body);
                })
                .build();
    }

    @Bean(name = "workOrderServiceRestClient")
    public RestClient workOrderServiceRestClient(
            @Value("${rest.client.url.WorkOrderService}") String workOrderServiceServiceUrl,
            @Value("${rest.client.config.WorkOrderService.connectionTimeout}") int workOrderServiceConnectionTimeout,
            @Value("${rest.client.config.WorkOrderService.readTimeout}") int workOrderServiceReadTimeout,
            PoolingHttpClientConnectionManager manager) {

        return RestClient.builder()
                .baseUrl(workOrderServiceServiceUrl)
                .requestFactory(clientHttpRequestFactory(workOrderServiceConnectionTimeout, workOrderServiceReadTimeout, manager))
                .requestInterceptor((request, body, execution) -> {
                    log.debug("=== REQUEST DEBUG ===");
                    log.debug("URI: {}", request.getURI());
                    log.debug("Method: {}", request.getMethod());
                    log.debug("Headers: {}", request.getHeaders());
                    log.debug("Body (raw): {}", new String(body));
                    return execution.execute(request, body);
                })
                .build();
    }


    @Bean
    public PoolingHttpClientConnectionManager poolingConnectionManager() {
        var connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(maxConnectionsPool);
        connectionManager.setDefaultMaxPerRoute(maxConnectionsPerRoute);

        // Registrar métricas una sola vez sobre el bean compartido
        Gauge.builder(METRIC_POOL_IDLE_NAME, connectionManager,
                        manager -> manager.getTotalStats().getAvailable())
                .tags(METRIC_POOL_NAME, METRIC_NAME)
                .description(METRIC_POOL_IDLE_DESC)
                .register(registry);

        Gauge.builder(METRIC_POOL_ACTIVE_NAME, connectionManager,
                        manager -> manager.getTotalStats().getLeased())
                .tags(METRIC_POOL_NAME, METRIC_NAME)
                .description(METRIC_POOL_ACTIVE_DESC)
                .register(registry);

        Gauge.builder(METRIC_POOL_PENDING_NAME, connectionManager,
                        manager -> manager.getTotalStats().getPending())
                .tags(METRIC_POOL_NAME, METRIC_NAME)
                .description(METRIC_POOL_PENDING_DESC)
                .register(registry);

        Gauge.builder(METRIC_POOL_MAX_NAME, connectionManager,
                        manager -> manager.getTotalStats().getMax())
                .tags(METRIC_POOL_NAME, METRIC_NAME)
                .description(METRIC_POOL_MAX_DESC)
                .register(registry);

        return connectionManager;
    }

    private ClientHttpRequestFactory clientHttpRequestFactory(int connectionTimeout, int readTimeout, PoolingHttpClientConnectionManager manager) {
        var requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(connectionTimeout))
                .setResponseTimeout(Timeout.ofMilliseconds(readTimeout))
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(manager) // reutiliza el pool compartido
                .setDefaultRequestConfig(requestConfig)
                .build();

        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }
}
