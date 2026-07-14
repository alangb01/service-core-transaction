package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.reactivex.rxjava3.core.Maybe;
import pe.nom.charlygastelo.app.transactionservice.domain.port.AccountClientPort;
import pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.client.dto.AccountResponse;

@Component
public class AccountClient implements AccountClientPort {

    private final WebClient webClient;

    public AccountClient(WebClient.Builder builder,
                         @Value("${client.account-service.base-url}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    @CircuitBreaker(name = "account-service", fallbackMethod = "fallbackAccountById")
    @TimeLimiter(name = "account-service")
    @Retry(name = "account-service")
    public Maybe<AccountResponse> getAccountById(String accountId, String token) {
        return Maybe.fromPublisher(
                webClient.get()
                        .uri("/api/accounts/{id}", accountId)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .retrieve()
                        .bodyToMono(AccountResponse.class)
        );
    }

    private Maybe<AccountResponse> fallbackAccountById(String accountId, Throwable ex) {
        return Maybe.just(new AccountResponse(
                accountId,
                null,
                null,
                null,
                false,
                "ACCOUNT_SERVICE_UNAVAILABLE"
        ));
    }
}
