package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.in.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.shared.avro.dto.CreditCardPaymentOccurredEvent;
import pe.nom.charlygastelo.app.transactionservice.application.usecase.TransactionProgressUseCase;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreditCardPaymentOccurredConsumer {

    private final TransactionProgressUseCase progressUseCase;

    @KafkaListener(topics = "${topic.credit-card-payment-occurred}", groupId = "transaction-service")
    public void consume(CreditCardPaymentOccurredEvent event) {

        String txId = event.getTransactionId().toString();
        String eventType = event.getEventType().toString();

        log.info("[TX] Received {} txId={}", eventType, txId);

        progressUseCase.markEventArrived(txId, eventType)
                .subscribe(
                        () -> log.info("[TX] Progress updated for txId={}", txId),
                        err -> log.error("[TX] Error processing txId={}, reason={}", txId, err.getMessage())
                );
    }
}
