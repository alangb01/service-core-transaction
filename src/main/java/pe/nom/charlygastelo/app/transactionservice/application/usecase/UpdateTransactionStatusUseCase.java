package pe.nom.charlygastelo.app.transactionservice.application.usecase;

import java.util.function.Supplier;
import org.springframework.stereotype.Service;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.transactionservice.domain.model.TransactionStatus;
import pe.nom.charlygastelo.app.transactionservice.domain.port.TransactionLedgerEventProducerPort;
import pe.nom.charlygastelo.app.transactionservice.domain.port.TransactionRepositoryPort;


@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateTransactionStatusUseCase {

    private final TransactionRepositoryPort transactionRepository;
    private final TransactionLedgerEventProducerPort eventProducer;

    public Completable markCompleted(String transactionId, String eventType) {
        log.info("[TX-USECASE] Marking COMPLETED txId={} eventType={}", transactionId, eventType);
        return updateStatus(
                transactionId,
                TransactionStatus.COMPLETED,
                () -> eventProducer.publishTransactionCompleted(transactionId),
                "COMPLETED",
                eventType
        );
    }

    public Completable markFailed(String transactionId, String reason) {
        log.info("[TX-USECASE] Marking FAILED txId={} reason={}", transactionId, reason);
        return updateStatus(
                transactionId,
                TransactionStatus.FAILED,
                () -> eventProducer.publishTransactionFailed(transactionId, reason),
                "FAILED",
                reason
        );
    }

    private Completable updateStatus(
            String transactionId,
            TransactionStatus newStatus,
            Supplier<Completable> eventPublisher,
            String statusLabel,
            String extraInfo
    ) {

        log.info("[TX-USECASE] Marking {} txId={} info={}", statusLabel, transactionId, extraInfo);

        return transactionRepository.findById(transactionId)
                .switchIfEmpty(Single.error(new RuntimeException(
                        "Transaction not found for txId=" + transactionId)))
                .flatMapCompletable(tx -> {

                    if (tx.status() == newStatus) {
                        log.info("[TX-USECASE] Transaction already {} txId={} — skipping update",
                                statusLabel, transactionId);
                        return Completable.complete();
                    }

                    log.info("[TX-USECASE] Updating transaction to {} txId={}", statusLabel, transactionId);

                    return transactionRepository.save(tx.withStatus(newStatus))
                            .doOnSuccess(saved ->
                                    log.info("[TX-USECASE] Transaction saved as {} txId={}",
                                            statusLabel, transactionId))
                            .ignoreElement()
                            .andThen(eventPublisher.get())
                            .doOnComplete(() ->
                                    log.info("[TX-USECASE] Transaction{}Event published txId={}",
                                            statusLabel, transactionId));
                })
                .doOnError(error ->
                        log.error("[TX-USECASE] Error marking {} txId={} reason={}",
                                statusLabel, transactionId, error.getMessage(), error));
    }
}
