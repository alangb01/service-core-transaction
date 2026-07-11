package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.in.event;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.shared.avro.dto.TransactionCompletedEvent;
import pe.nom.charlygastelo.app.shared.avro.dto.TransactionFailedEvent;
import pe.nom.charlygastelo.app.transactionservice.application.usecase.CompleteTransactionUseCase;
import pe.nom.charlygastelo.app.transactionservice.application.usecase.FailTransactionUseCase;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionEventConsumer {

    private final CompleteTransactionUseCase completeTransactionUseCase;
    private final FailTransactionUseCase failTransactionUseCase;

    @KafkaListener(
            topics = "${topic.transaction-completed}",
            groupId = "transaction-service")
    public void consumeTransactionCompleted(TransactionCompletedEvent event) {
        try {

            String transactionId = event.getTransactionId().toString();

            log.info("TransactionCompletedEvent received. transactionId={}", transactionId);

            completeTransactionUseCase.execute(transactionId)
                    .subscribe(
                            saved -> log.info(
                                    "Transaction status updated to COMPLETED. transactionId={}",
                                    saved.id()
                            ),
                            error -> log.error(
                                    "Error updating transaction to COMPLETED. transactionId={}, reason={}",
                                    transactionId,
                                    error.getMessage(),
                                    error
                            )
                    );

        }
        catch (Exception e) {
            log.error("Error consuming TransactionCompletedEvent", e);
        }
    }

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