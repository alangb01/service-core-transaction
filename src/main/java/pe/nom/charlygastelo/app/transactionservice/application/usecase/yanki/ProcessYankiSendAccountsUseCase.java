package pe.nom.charlygastelo.app.transactionservice.application.usecase.yanki;

import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.shared.avro.dto.DebitCardAccountsResolvedEvent;
import pe.nom.charlygastelo.app.transactionservice.application.usecase.CompleteTransactionUseCase;
import pe.nom.charlygastelo.app.transactionservice.application.usecase.TransactionProgressUseCase;
import pe.nom.charlygastelo.app.transactionservice.domain.model.Transaction;
import pe.nom.charlygastelo.app.transactionservice.domain.model.TransactionStatus;
import pe.nom.charlygastelo.app.transactionservice.domain.port.TransactionRepositoryPort;


@Component
@Slf4j
@RequiredArgsConstructor
public class ProcessYankiSendAccountsUseCase {
    private final TransactionRepositoryPort transactionRepository;
    private final TransactionProgressUseCase transactionProgressUseCase;
    private final CompleteTransactionUseCase completeTransactionUseCase;

    public Completable execute(DebitCardAccountsResolvedEvent event) {

        String txId = event.getTransactionId().toString();
        String eventType = event.getEventType().toString();
        String sourceAccountId = event.getSourceAccountId().toString();
        String targetAccountId = event.getTargetAccountId().toString();


        log.info("[TX] Processing accounts resolved for txId={}", txId);

        return transactionRepository.findById(txId)
                .switchIfEmpty(Single.error(new RuntimeException("Transaction not found")))
                .flatMapCompletable(tx -> {

                    // 1. Validar estado
                    if (!tx.status().equals(TransactionStatus.PENDING)) {
                        return Completable.error(new RuntimeException("Invalid state for accounts resolved"));
                    }

                    // 2. Actualizar transacción
                    Transaction transactionUpdated = tx.updateForYankiSend(sourceAccountId,
                            targetAccountId, TransactionStatus.PROCESSING);

                    // 3. Guardar transacción
                    return transactionRepository.save(transactionUpdated)
                            .flatMapCompletable(saved -> {
                                Completable progress =  transactionProgressUseCase.markEventArrived(txId, eventType);

                                completeTransactionUseCase.execute(saved.id()).subscribe();

                                return progress;
                            });
                });
    }
}
