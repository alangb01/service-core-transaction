package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.event;

import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.shared.avro.dto.DebitCardRequestEvent;
import pe.nom.charlygastelo.app.transactionservice.domain.model.DebitCard;
import pe.nom.charlygastelo.app.transactionservice.domain.port.DebitCardRepositoryPort;


@Component
@RequiredArgsConstructor
public class DebitCardKafkaClient implements DebitCardRepositoryPort {

    private final DebitCardRequestProducer DebitCardRequestProducer;
    private final DebitCardResponseRegistry DebitCardResponseRegistry;

//    @Override
    public Single<DebitCard> requestByIdV1(String DebitCardId) {
        String correlationId = UUID.randomUUID().toString();

        DebitCardRequestEvent event = DebitCardRequestEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("DebitCard_REQUEST")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("transaction-service")
                .setCorrelationId(correlationId)
                .setDebitCardId(DebitCardId)
                .build();

        return DebitCardResponseRegistry.waitForResponse(correlationId)
                .doOnSubscribe(disposable ->
                        DebitCardRequestProducer.send(correlationId, event)
                );
    }

    @Override
    public Single<DebitCard> requestById(String DebitCardId) {
        String correlationId = UUID.randomUUID().toString();

        DebitCardRequestEvent event = DebitCardRequestEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("DEBIT_CARD_REQUEST")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("transaction-service")
                .setCorrelationId(correlationId)
                .setDebitCardId(DebitCardId)
                .build();

        return DebitCardResponseRegistry.waitForResponse(correlationId)
                .doOnSubscribe(disposable ->
                        DebitCardRequestProducer.send(correlationId, event)
                );
    }
}