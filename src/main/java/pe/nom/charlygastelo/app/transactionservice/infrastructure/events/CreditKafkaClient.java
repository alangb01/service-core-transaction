package pe.nom.charlygastelo.app.transactionservice.infrastructure.events;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;

import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.shared.avro.dto.CreditRequestEvent;
import pe.nom.charlygastelo.app.shared.avro.dto.OverdueDebtRequestEvent;
import pe.nom.charlygastelo.app.transactionservice.domain.model.Credit;
import pe.nom.charlygastelo.app.transactionservice.domain.port.CreditEventPort;

@Component
@RequiredArgsConstructor
public class CreditKafkaClient implements CreditEventPort {

    private final CreditRequestProducer creditRequestProducer;
    private final CreditResponseRegistry creditResponseRegistry;

    private final OverdueDebtRequestProducer overdueDebtRequestProducer;
    private final OverdueDebtResponseRegistry overdueDebtResponseRegistry;

    @Override
    public Single<Credit> getById(String creditId) {
        String correlationId = UUID.randomUUID().toString();

        CreditRequestEvent event = CreditRequestEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("CREDIT_REQUEST")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("transaction-service")
                .setCorrelationId(correlationId)
                .setCreditId(creditId)
                .build();

        return creditResponseRegistry.waitForResponse(correlationId)
                .doOnSubscribe(disposable ->
                        creditRequestProducer.send(correlationId, event)
                );
    }

    @Override
    public Single<Boolean> hasOverdueDebt(String customerId) {
        String correlationId = UUID.randomUUID().toString();

        OverdueDebtRequestEvent event = OverdueDebtRequestEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("OVERDUE_DEBT_REQUEST")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("transaction-service")
                .setCorrelationId(correlationId)
                .setCustomerId(customerId)
                .build();

        return overdueDebtResponseRegistry.waitForResponse(correlationId)
                .doOnSubscribe(disposable ->
                        overdueDebtRequestProducer.send(correlationId, event)
                );
    }
}