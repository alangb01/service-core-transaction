package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.client;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.client.dto.ValidateRequest;
import pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.client.dto.ValidateResponse;
import reactor.core.publisher.Mono;

@Component
public class AuthClientAdapter {

    private final WebClient webClient;

    public AuthClientAdapter(WebClient.Builder builder,
                             @Value("${client.auth-service.base-url}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public Mono<ValidateResponse> validate(String token) {
        return webClient.post()
                .uri("/api/auth/validate")
                .bodyValue(new ValidateRequest(token))
                .retrieve()
                .bodyToMono(ValidateResponse.class);
    }
}