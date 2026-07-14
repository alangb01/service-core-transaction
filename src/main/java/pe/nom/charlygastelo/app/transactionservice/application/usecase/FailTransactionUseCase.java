package pe.nom.charlygastelo.app.transactionservice.application.usecase;

import java.time.Instant;
import org.springframework.stereotype.Service;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.transactionservice.domain.exception.TransactionNotFoundException;
import pe.nom.charlygastelo.app.transactionservice.domain.model.Transaction;
import pe.nom.charlygastelo.app.transactionservice.domain.model.TransactionStatus;
import pe.nom.charlygastelo.app.transactionservice.domain.port.TransactionRepositoryPort;

@Service
@Slf4j
@RequiredArgsConstructor
public class FailTransactionUseCase {

    private final TransactionRepositoryPort repository;

    public Single<Transaction> execute(String transactionId, String reason) {
        log.warn("Failing transaction. transactionId={}, reason={}", transactionId, reason);

        return repository.findById(transactionId)
                .switchIfEmpty(Single.error(
                        new TransactionNotFoundException("Transaction not found: " + transactionId)
                ))
                .map(tx -> markAsFailed(tx, reason))
                .flatMap(repository::save)
                .doOnSuccess(saved ->
                        log.warn("Transaction marked as FAILED. transactionId={}, reason={}",
                                saved.id(), reason))
                .doOnError(error ->
                        log.error("Error failing transaction. transactionId={}, reason={}",
                                transactionId, error.getMessage(), error));
    }

    private Transaction markAsFailed(Transaction tx, String reason) {
        return new Transaction(
                tx.id(),
                tx.customerId(),
                tx.sourceProductId(),
                tx.targetProductId(),
                tx.sourceProductType(),
                tx.targetProductType(),
                tx.type(),
                TransactionStatus.FAILED,
                tx.amount(),
                tx.commission(),
                reason == null ? tx.description() : reason,
                tx.createdAt(),
                Instant.now()
        );
    }
}