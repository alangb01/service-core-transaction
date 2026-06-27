package pe.nom.charlygastelo.app.transactionservice.infrastructure.client;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import pe.nom.charlygastelo.app.transactionservice.infrastructure.client.dto.LimitCheckRequest;
import pe.nom.charlygastelo.app.transactionservice.infrastructure.client.dto.LimitCheckResponse;
import reactor.core.publisher.Mono;

@Component
public class LimitClient {

    private final WebClient webClient;


    public LimitClient(WebClient.Builder builder,
                       @Value("${client.limit-service.base-url}") String baseUrl) {

        this.webClient = builder.baseUrl(baseUrl).build();
    }

    @CircuitBreaker(name = "limitService", fallbackMethod = "fallbackLimit")
    @TimeLimiter(name = "limitService")
    @Retry(name = "limitService")
    public Mono<LimitCheckResponse> validateLimit(String customerId,
                                                  String accountId,
                                                  BigDecimal amount) {

        LimitCheckRequest request = new LimitCheckRequest(
                customerId,
                accountId,
                amount
        );

        return webClient.post()
                .uri("/api/limits/check")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(LimitCheckResponse.class);
    }

    private Mono<LimitCheckResponse> fallbackLimit(String customerId,
                                                   String accountId,
                                                   BigDecimal amount,
                                                   Throwable ex) {
        return Mono.just(new LimitCheckResponse(
                false,
                "LIMIT_SERVICE_UNAVAILABLE"
        ));
    }
}
