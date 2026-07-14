package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.in.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.shared.avro.dto.MovementRecordedEvent;
import pe.nom.charlygastelo.app.transactionservice.application.usecase.TransactionProgressUseCase;

@Component
@RequiredArgsConstructor
@Slf4j
public class MovementRecordedConsumer {

    private final TransactionProgressUseCase progressUseCase;

    @KafkaListener(topics = "${topic.movement-recorded}", groupId = "transaction-service")
    public void consume(MovementRecordedEvent event) {

        String txId = event.getTransactionId().toString();
        String eventType = "MOVEMENT_RECORDED";
        String movementType = event.getMovementType().toString();

        log.info("[TX] Received {} txId={}", eventType, txId);

        progressUseCase.markEventArrived(txId, eventType)
                .subscribe(
                        () -> log.info("[TX] Progress updated for eventType={} txId={} movement={}", eventType, txId, movementType),
                        err -> log.error("[TX] Error processing txId={}, reason={}", txId, err.getMessage())
                );
    }
}
