package pe.nom.charlygastelo.app.transactionservice.infrastructure.client;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import pe.nom.charlygastelo.app.transactionservice.infrastructure.client.dto.FraudCheckRequest;
import pe.nom.charlygastelo.app.transactionservice.infrastructure.client.dto.FraudCheckResponse;
import reactor.core.publisher.Mono;

@Component
public class FraudClient {

    private final WebClient webClient;

    public FraudClient(WebClient.Builder builder,
                       @Value("${client.fraud-service.base-url}") String baseUrl) {

        this.webClient = builder.baseUrl(baseUrl).build();
    }

    @CircuitBreaker(name = "fraudService", fallbackMethod = "fallbackFraud")
    @TimeLimiter(name = "fraudService")
    @Retry(name = "fraudService")
    public Mono<FraudCheckResponse> evaluateFraud(String transactionId,
                                                  String customerId,
                                                  String accountId,
                                                  BigDecimal amount) {

        FraudCheckRequest request = new FraudCheckRequest(
                transactionId,
                customerId,
                accountId,
                amount
        );

        return webClient.post()
                .uri("/api/fraud/transaction/check")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(FraudCheckResponse.class);
    }

    private Mono<FraudCheckResponse> fallbackFraud(String transactionId,
                                                   String customerId,
                                                   String accountId,
                                                   BigDecimal amount,
                                                   Throwable ex) {
        return Mono.just(new FraudCheckResponse(
                false,
                "FRAUD_SERVICE_UNAVAILABLE"
        ));
    }
}
