package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.in.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.shared.avro.dto.TransactionFailedEvent;
import pe.nom.charlygastelo.app.transactionservice.application.usecase.FailTransactionUseCase;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionFailedConsumer {
    private final FailTransactionUseCase failTransactionUseCase;

    @KafkaListener(
            topics = "${topic.transaction-failed}",
            groupId = "transaction-service"
    )
    public void consumeTransactionFailed(TransactionFailedEvent event) {
        try {

            String transactionId = event.getTransactionId().toString();
            String reason = event.getReason().toString();

            log.warn("TransactionFailedEvent received. transactionId={}, reason={}",
                    transactionId, reason);

            failTransactionUseCase.execute(transactionId, reason)
                    .subscribe(
                            saved -> log.warn(
                                    "Transaction status updated to FAILED. transactionId={}, reason={}",
                                    saved.id(),
                                    reason
                            ),
                            error -> log.error(
                                    "Error updating transaction to FAILED. transactionId={}, reason={}",
                                    transactionId,
                                    error.getMessage(),
                                    error
                            )
                    );

        }
        catch (Exception e) {
            log.error("Error consuming TransactionFailedEvent", e);
        }
    }
}