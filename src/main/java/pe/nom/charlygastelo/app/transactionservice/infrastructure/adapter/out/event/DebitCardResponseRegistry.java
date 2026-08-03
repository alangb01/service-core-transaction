package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.shared.avro.dto.DebitCardResponseEvent;
import pe.nom.charlygastelo.app.transactionservice.domain.exception.CreditNotFoundException;
import pe.nom.charlygastelo.app.transactionservice.domain.model.DebitCard;

@Component
@Slf4j
public class DebitCardResponseRegistry {

    private final Map<String, SingleEmitter<DebitCard>> pendingRequests =
            new ConcurrentHashMap<>();

    public Single<DebitCard> waitForResponse(String correlationId) {
        return Single.<DebitCard>create(emitter ->
                        pendingRequests.put(correlationId, emitter)
                ).timeout(2, TimeUnit.SECONDS)
                .doFinally(() -> pendingRequests.remove(correlationId));
    }

    public void complete(DebitCardResponseEvent event) {
        log.info("DebitCardResponseEvent correlationId {} debitcard {} accountId {} status {}",
                event.getCorrelationId(),
                event.getDebitCard().getId(),
                event.getDebitCard().getAccountId(),
                event.getDebitCard().getStatus()
        );
        String correlationId = event.getCorrelationId().toString();

        SingleEmitter<DebitCard> emitter =
                pendingRequests.remove(correlationId);

        if (emitter == null) {
            log.warn("No pending debit card request found for correlationId={}", correlationId);
            return;
        }

        if (!event.getFound()) {
            emitter.onError(new CreditNotFoundException("Debit card not found"));
            return;
        }

        DebitCard debitCard = new DebitCard(
            event.getDebitCard().getId().toString(),
            event.getDebitCard().getAccountId().toString(),
            event.getDebitCard().getStatus().toString()
        );

        emitter.onSuccess(debitCard);
    }
}