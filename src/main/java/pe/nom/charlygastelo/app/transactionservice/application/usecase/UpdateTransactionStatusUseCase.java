package pe.nom.charlygastelo.app.transactionservice.application.usecase;

import io.reactivex.rxjava3.core.Single;
import org.springframework.stereotype.Service;
import io.reactivex.rxjava3.core.Completable;
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

        return transactionRepository.findById(transactionId)
                .switchIfEmpty(Single.error(new RuntimeException(
                        "Transaction not found for txId=" + transactionId)))
                .flatMapCompletable(tx -> {

                    if (tx.status() == TransactionStatus.COMPLETED) {
                        log.info("[TX-USECASE] Transaction already COMPLETED txId={} — skipping update", transactionId);
                        return Completable.complete();
                    }

                    log.info("[TX-USECASE] Updating transaction to COMPLETED txId={}", transactionId);

                    return transactionRepository.save(tx.withStatus(TransactionStatus.COMPLETED))
                            .doOnSuccess(saved ->
                                    log.info("[TX-USECASE] Transaction saved as COMPLETED txId={}", transactionId))
                            .ignoreElement()
                            .andThen(eventProducer.publishTransactionCompleted(transactionId))
                            .doOnComplete(() ->
                                    log.info("[TX-USECASE] TransactionCompletedEvent published txId={}", transactionId));
                })
                .doOnError(error ->
                        log.error("[TX-USECASE] Error marking COMPLETED txId={} reason={}",
                                transactionId, error.getMessage(), error));
    }

    public Completable markFailed(String transactionId, String reason) {

        log.info("[TX-USECASE] Marking FAILED txId={} reason={}", transactionId, reason);

        return transactionRepository.findById(transactionId)
                .switchIfEmpty(Single.error(new RuntimeException(
                        "Transaction not found for txId=" + transactionId)))
                .flatMapCompletable(tx -> {

                    if (tx.status() == TransactionStatus.FAILED) {
                        log.info("[TX-USECASE] Transaction already FAILED txId={} — skipping update", transactionId);
                        return Completable.complete();
                    }

                    log.info("[TX-USECASE] Updating transaction to FAILED txId={}", transactionId);

                    return transactionRepository.save(tx.withStatus(TransactionStatus.FAILED))
                            .doOnSuccess(saved ->
                                    log.info("[TX-USECASE] Transaction saved as FAILED txId={}", transactionId))
                            .ignoreElement()
                            .andThen(eventProducer.publishTransactionFailed(transactionId, reason))
                            .doOnComplete(() ->
                                    log.info("[TX-USECASE] TransactionFailedEvent published txId={} reason={}",
                                            transactionId, reason));
                })
                .doOnError(error ->
                        log.error("[TX-USECASE] Error marking FAILED txId={} reason={}",
                                transactionId, error.getMessage(), error));
    }

}
