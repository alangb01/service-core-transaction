package pe.nom.charlygastelo.app.transactionservice.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import pe.nom.charlygastelo.app.transactionservice.infrastructure.client.dto.AccountResponse;
import reactor.core.publisher.Mono;

@Component
public class AccountClient {

    private final WebClient webClient;

    public AccountClient(WebClient.Builder builder,
                         @Value("${client.account-service.base-url}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    @CircuitBreaker(name = "accountService", fallbackMethod = "fallbackAccount")
    @TimeLimiter(name = "accountService")
    @Retry(name = "accountService")
    public Mono<AccountResponse> getAccount(String accountId) {
        return webClient.get()
                .uri("/api/accounts/{id}", accountId)
                .retrieve()
                .bodyToMono(AccountResponse.class);
    }

    private Mono<AccountResponse> fallbackAccount(String accountId, Throwable ex) {
        return Mono.just(new AccountResponse(
                accountId,
                null,
                null,
                null,
                false,
                "ACCOUNT_SERVICE_UNAVAILABLE"
        ));
    }
}
