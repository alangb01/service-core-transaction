package pe.nom.charlygastelo.app.transactionservice.infrastructure.events;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.shared.avro.dto.CreditResponseEvent;
import pe.nom.charlygastelo.app.transactionservice.domain.exception.CreditNotFoundException;
import pe.nom.charlygastelo.app.transactionservice.domain.model.Credit;
import pe.nom.charlygastelo.app.transactionservice.domain.model.CreditStatus;
import pe.nom.charlygastelo.app.transactionservice.domain.model.CreditType;

@Component
@Slf4j
public class CreditResponseRegistry {

    private final Map<String, SingleEmitter<Credit>> pendingRequests =
            new ConcurrentHashMap<>();

    public Single<Credit> waitForResponse(String correlationId) {
        return Single.<Credit>create(emitter ->
                        pendingRequests.put(correlationId, emitter)
                ).timeout(2, TimeUnit.SECONDS)
                .doFinally(() -> pendingRequests.remove(correlationId));
    }

    public void complete(CreditResponseEvent event) {
        String correlationId = event.getCorrelationId().toString();

        SingleEmitter<Credit> emitter =
                pendingRequests.remove(correlationId);

        if (emitter == null) {
            log.warn("No pending credit request found for correlationId={}", correlationId);
            return;
        }

        if (!event.getFound()) {
            emitter.onError(new CreditNotFoundException("Credit not found"));
            return;
        }

        Credit credit = new Credit(
                event.getCreditId().toString(),
                event.getCustomerId().toString(),
                event.getNumber().toString(),
                CreditType.valueOf(event.getType().toString()),
                CreditStatus.valueOf(event.getStatus().toString()),
                BigDecimal.valueOf(event.getCreditLimit()),
                BigDecimal.valueOf(event.getBalance()),
                BigDecimal.valueOf(event.getAvailableBalance()),
                BigDecimal.ZERO,
                0,
                null,
                event.getOverdue(),
                null,
                null
        );

        emitter.onSuccess(credit);
    }
}