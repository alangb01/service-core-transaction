package pe.nom.charlygastelo.app.transactionservice.application.usecase;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.transactionservice.domain.exception.TransactionNotFoundException;
import pe.nom.charlygastelo.app.transactionservice.domain.port.TransactionEventProducerPort;
import pe.nom.charlygastelo.app.transactionservice.domain.port.TransactionRepositoryPort;

@Slf4j
@RequiredArgsConstructor
public class DeleteTransactionUseCase {

    private final TransactionRepositoryPort repository;
    private final TransactionEventProducerPort producer;

    public Completable execute(String id) {
        log.info("Starting transaction deletion process. id={}", id);

        return repository.findById(id)
                .switchIfEmpty(Single.error(
                        new TransactionNotFoundException("Transaction not found: " + id)
                ))
                .flatMapCompletable(transaction ->
                        repository.deleteById(id)
                                .doOnComplete(() ->
                                        log.info("Transaction deleted successfully. id={}", id)
                                )
                                .andThen(
                                        producer.publishTransactionDeleted(transaction)
                                                .doOnComplete(() ->
                                                        log.info("TransactionDeletedEvent published. id={}", id)
                                                )
                                )
                )
                .doOnError(error ->
                        log.error("Error deleting transaction {}: {}",
                                id, error.getMessage(), error)
                );
    }
}