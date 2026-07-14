package pe.nom.charlygastelo.app.transactionservice.application.usecase;

import java.time.Instant;
import org.springframework.stereotype.Service;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.transactionservice.domain.exception.TransactionNotFoundException;
import pe.nom.charlygastelo.app.transactionservice.domain.model.Transaction;
import pe.nom.charlygastelo.app.transactionservice.domain.model.TransactionStatus;
import pe.nom.charlygastelo.app.transactionservice.domain.port.MovementEventProducerPort;
import pe.nom.charlygastelo.app.transactionservice.domain.port.TransactionRepositoryPort;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompleteTransactionUseCase {

    private final TransactionRepositoryPort repository;
    private final MovementEventProducerPort movementProducer;

    public Single<Transaction> execute(String transactionId) {
        log.info("Completing transaction. transactionId={}", transactionId);

        return repository.findById(transactionId)
                .switchIfEmpty(Single.error(
                        new TransactionNotFoundException("Transaction not found: " + transactionId)
                ))
                .map(this::markAsCompleted)
                .flatMap(repository::save)
                .flatMap(saved -> {

                    log.info("Transaction marked as COMPLETED. transactionId={}", saved.id());

                    Completable movementFlow;

                    switch (saved.type()) {
                        case DEPOSIT:
                            movementFlow = movementProducer.publishMovementCreditCreated(saved);
                            break;

                        case WITHDRAW:
                            movementFlow = movementProducer.publishMovementDebitCreated(saved);
                            break;

                        case TRANSFER, TRANSFER_TO_THIRD:
                            movementFlow = movementProducer.publishMovementDebitCreated(saved)   // origen
                                            .andThen(movementProducer.publishMovementCreditCreated(saved)); // destino
                            break;
                        case CREDIT_WITHDRAW, CREDIT_PAYMENT:
                            movementFlow = movementProducer.publishMovementDebitCreated(saved)
                                    .andThen(movementProducer.publishMovementCreditCreated(saved));
                            break;
                        default:
                            movementFlow = Completable.complete();
                    }

                    // Encadenar movimientos dentro del flujo
                    return movementFlow.andThen(Single.just(saved));
                })
                .doOnError(error ->
                        log.error("Error completing transaction. transactionId={}, reason={}",
                                transactionId, error.getMessage(), error)
                );
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
                Instant.now()
        );
    }
}