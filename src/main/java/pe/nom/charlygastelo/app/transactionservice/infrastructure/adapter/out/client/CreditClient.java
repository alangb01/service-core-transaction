package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.client;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.reactivex.rxjava3.core.Maybe;
import pe.nom.charlygastelo.app.transactionservice.domain.port.CreditClientPort;
import pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.client.dto.CreditResponse;
import reactor.core.publisher.Mono;


@Component
public class CreditClient implements CreditClientPort {
    private final WebClient webClient;

    public CreditClient(WebClient.Builder builder,
                         @Value("${client.credit-service.base-url}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    @CircuitBreaker(name = "credit-service", fallbackMethod = "fallbackGetCreditById")
    @TimeLimiter(name = "credit-service")
    @Retry(name = "credit-service")
    public Maybe<CreditResponse> getCreditById(String creditId, String token) {
        return Maybe.fromPublisher(
                webClient.get()
                        .uri("/credits/{id}", creditId)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .retrieve()
                        .bodyToMono(CreditResponse.class)
        );
    }

    private Mono<CreditResponse> fallbackGetCreditById(String creditId, Throwable ex) {
        return Mono.just(new CreditResponse(
                    creditId,
                null,
                BigDecimal.ZERO,
                null,
                false,
                "ACCOUNT_SERVICE_UNAVAILABLE"
        ));
    }
}
