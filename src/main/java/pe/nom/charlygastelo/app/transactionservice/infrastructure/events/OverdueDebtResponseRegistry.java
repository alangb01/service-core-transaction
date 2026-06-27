package pe.nom.charlygastelo.app.transactionservice.infrastructure.events;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.shared.avro.dto.OverdueDebtResponseEvent;

@Component
@Slf4j
public class OverdueDebtResponseRegistry {

    private final Map<String, SingleEmitter<Boolean>> pendingRequests =
            new ConcurrentHashMap<>();

    public Single<Boolean> waitForResponse(String correlationId) {
        return Single.<Boolean>create(emitter ->
                        pendingRequests.put(correlationId, emitter)
                ).timeout(2, TimeUnit.SECONDS)
                .doFinally(() -> pendingRequests.remove(correlationId));
    }

    public void complete(OverdueDebtResponseEvent event) {
        String correlationId = event.getCorrelationId().toString();

        SingleEmitter<Boolean> emitter = pendingRequests.remove(correlationId);

        if (emitter == null) {
            log.warn("No pending overdue debt request. correlationId={}", correlationId);
            return;
        }

        emitter.onSuccess(event.getHasOverdueDebt());
    }
}