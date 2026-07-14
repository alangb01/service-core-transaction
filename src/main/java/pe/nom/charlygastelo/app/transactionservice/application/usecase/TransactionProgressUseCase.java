package pe.nom.charlygastelo.app.transactionservice.application.usecase;

import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.transactionservice.domain.model.TransactionProgress;
import pe.nom.charlygastelo.app.transactionservice.domain.model.TransactionType;
import pe.nom.charlygastelo.app.transactionservice.domain.port.TransactionProgressRepositoryPort;


@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionProgressUseCase {

    private final TransactionProgressRepositoryPort progressRepository;
    private final UpdateTransactionStatusUseCase updateStatusUseCase;

    /**
     * Matriz de eventos requeridos por tipo de transacción.
     */
    private static final Map<TransactionType, Set<String>> REQUIRED_EVENTS = Map.of(
            TransactionType.DEPOSIT, Set.of(
                    "ACCOUNT_DEPOSIT_OCCURRED",
                    "MOVEMENT_RECORDED"
            ),
            TransactionType.WITHDRAW, Set.of(
                    "ACCOUNT_WITHDRAW_OCCURRED",
                    "MOVEMENT_RECORDED"
            ),
            TransactionType.TRANSFER, Set.of(
                    "ACCOUNT_WITHDRAW_OCCURRED",
                    "ACCOUNT_DEPOSIT_OCCURRED",
                    "MOVEMENT_RECORDED"
            ),
            TransactionType.CREDIT_PAYMENT, Set.of(
                    "ACCOUNT_WITHDRAW_OCCURRED",
                    "CREDIT_PAYMENT_OCCURRED",
                    "MOVEMENT_RECORDED"
            ),
            TransactionType.CREDIT_WITHDRAW, Set.of(
                    "CREDIT_WITHDRAW_OCCURRED",
                    "MOVEMENT_RECORDED"
            ),
            TransactionType.CREDIT_CARD_CHARGE, Set.of(
                    "CREDIT_CARD_CHARGE_OCCURRED",
                    "MOVEMENT_RECORDED"
            )
    );

    /**
     * Marca un evento como recibido y decide si completar la transacción.
     */
    public Completable markEventArrived(String transactionId, String eventType) {

        log.info("[TX-PROGRESS] Marking event {} for txId={}", eventType, transactionId);

//        return progressRepository.findByTransactionId(transactionId)
//                .switchIfEmpty(Single.error(new RuntimeException(
//                        "TransactionProgress not found for txId=" + transactionId)))
//                .map(progress -> progress.mark(eventType))
//                .flatMap(progressRepository::save)
        return progressRepository.updateProgress(transactionId, eventType)
                .andThen(progressRepository.findByTransactionId(transactionId))
                .flatMapCompletable(progress -> {
                    System.out.println(progress);
                    if (progress.failed()) {
                        log.info("[TX-PROGRESS] Transaction FAILED txId={}", transactionId);
                        return updateStatusUseCase.markFailed(transactionId, "FAILED");
                    }

                    if (isCompleted(progress)) {
                        log.info("[TX-PROGRESS] Transaction COMPLETED txId={}", transactionId);
                        return updateStatusUseCase.markCompleted(transactionId, eventType);
                    }

                    log.info("[TX-PROGRESS] Transaction still pending txId={}", transactionId);
                    return Completable.complete();
                });
    }

    /**
     * Verifica si ya llegaron todos los eventos requeridos.
     */
    public boolean isCompleted(TransactionProgress progress) {
        return REQUIRED_EVENTS.get(progress.type())
                .stream()
                .allMatch(progress::hasEvent);
    }
}
