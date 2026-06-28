package pe.nom.charlygastelo.app.transactionservice.application.usecase;

import java.time.LocalDateTime;

import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.transactionservice.domain.exception.TransactionNotFoundException;
import pe.nom.charlygastelo.app.transactionservice.domain.model.Transaction;
import pe.nom.charlygastelo.app.transactionservice.domain.model.TransactionStatus;
import pe.nom.charlygastelo.app.transactionservice.domain.port.TransactionRepositoryPort;

@Slf4j
@RequiredArgsConstructor
public class CompleteTransactionUseCase {

    private final TransactionRepositoryPort repository;

    public Single<Transaction> execute(String transactionId) {
        log.info("Completing transaction. transactionId={}", transactionId);

        return repository.findById(transactionId)
                .switchIfEmpty(Single.error(
                        new TransactionNotFoundException("Transaction not found: " + transactionId)
                ))
                .map(this::markAsCompleted)
                .flatMap(repository::save)
                .doOnSuccess(saved ->
                        log.info("Transaction marked as COMPLETED. transactionId={}", saved.id()))
                .doOnError(error ->
                        log.error("Error completing transaction. transactionId={}, reason={}",
                                transactionId, error.getMessage(), error));
    }

    private Transaction markAsCompleted(Transaction tx) {
        return new Transaction(
                tx.id(),
                tx.customerId(),
                tx.sourceProductId(),
                tx.targetProductId(),
                tx.sourceProductType(),
                tx.targetProductType(),
                tx.type(),
                TransactionStatus.COMPLETED,
                tx.amount(),
                tx.commission(),
                tx.description(),
                tx.createdAt(),
                LocalDateTime.now()
        );
    }
}