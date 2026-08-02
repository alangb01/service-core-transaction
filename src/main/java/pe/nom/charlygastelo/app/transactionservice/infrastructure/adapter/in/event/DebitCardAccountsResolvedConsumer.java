package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.in.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.shared.avro.dto.DebitCardAccountsResolvedEvent;
import pe.nom.charlygastelo.app.transactionservice.application.usecase.TransactionProgressUseCase;
import pe.nom.charlygastelo.app.transactionservice.application.usecase.yanki.ProcessYankiSendAccountsUseCase;

@Component
@RequiredArgsConstructor
@Slf4j
public class DebitCardAccountsResolvedConsumer {

    private final ProcessYankiSendAccountsUseCase processYankiSendUseCase;
    private final TransactionProgressUseCase progressUseCase;

    @KafkaListener(
            topics = "${topic.debit-card-accounts-resolved}",
            groupId = "transaction-service"
    )
    public void consume(DebitCardAccountsResolvedEvent event) {

        String txId = event.getTransactionId().toString();
        String eventType = event.getEventType().toString();

        log.info("[TX] Received {} txId={}", eventType, txId);

        processYankiSendUseCase.execute(event)
            .subscribe(
                () -> log.info("[TX] Progress updated for txId={}", txId),
                err -> log.error("[TX] Error processing txId={}, reason={}", txId, err.getMessage())
            );
    }
}
