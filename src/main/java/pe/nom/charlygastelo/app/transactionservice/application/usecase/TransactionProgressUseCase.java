package pe.nom.charlygastelo.app.transactionservice.application.usecase;

import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import io.reactivex.rxjava3.core.Completable;
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


    private static final Map<TransactionType, Set<String>> REQUIRED_EVENTS =
            Map.ofEntries(
                Map.entry(
                    TransactionType.DEPOSIT,
                    Set.of(
                        "ACCOUNT_DEPOSIT_OCCURRED",
                        "MOVEMENT_TARGET_RECORDED"
                    )
                ),
                Map.entry(
                    TransactionType.WITHDRAW, Set.of(
                        "ACCOUNT_WITHDRAW_OCCURRED",
                        "MOVEMENT_SOURCE_RECORDED"
                    )
                ),
                Map.entry(
                    TransactionType.TRANSFER, Set.of(
                        "ACCOUNT_WITHDRAW_OCCURRED",
                        "ACCOUNT_DEPOSIT_OCCURRED",
                        "MOVEMENT_SOURCE_RECORDED",
                        "MOVEMENT_TARGET_RECORDED"
                    )
                ),
                Map.entry(
                    TransactionType.TRANSFER_TO_THIRD, Set.of(
                        "ACCOUNT_WITHDRAW_OCCURRED",
                        "ACCOUNT_DEPOSIT_OCCURRED",
                        "MOVEMENT_SOURCE_RECORDED",
                        "MOVEMENT_TARGET_RECORDED"
                    )
                ),
                //CREDIT
                Map.entry(
                    TransactionType.CREDIT_PAYMENT, Set.of(
                        "ACCOUNT_WITHDRAW_OCCURRED",
                        "CREDIT_PAYMENT_OCCURRED",
                        "MOVEMENT_SOURCE_RECORDED",
                        "MOVEMENT_TARGET_RECORDED"
                    )
                ),
                Map.entry(
                    TransactionType.CREDIT_WITHDRAW, Set.of(
                        "CREDIT_WITHDRAW_OCCURRED",
                        "ACCOUNT_DEPOSIT_OCCURRED",
                        "MOVEMENT_SOURCE_RECORDED",
                        "MOVEMENT_TARGET_RECORDED"
                    )
                ),
                //CARD
                Map.entry(
                    TransactionType.CREDIT_CARD_CHARGE, Set.of(
                        "CREDIT_CARD_CHARGE_OCCURRED",
                        "MOVEMENT_SOURCE_RECORDED"
                    )
                ),
                Map.entry(
                    TransactionType.CREDIT_CARD_PAYMENT, Set.of(
                        "CREDIT_CARD_CHARGE_OCCURRED",
                        "MOVEMENT_TARGET_RECORDED"
                    )
                ),
                Map.entry(
                    TransactionType.DEBIT_CARD_PURCHASE, Set.of(
                        "DEBIT_CARD_CHARGE_OCCURRED",
                        "ACCOUNT_WITHDRAW_OCCURRED",
                        "MOVEMENT_SOURCE_RECORDED",
                        "MOVEMENT_TARGET_RECORDED"
                    )
                ),
                Map.entry(
                    TransactionType.DEBIT_CARD_PAYMENT, Set.of(
                        "DEBIT_CARD_CHARGE_OCCURRED",
                        "ACCOUNT_WITHDRAW_OCCURRED",
                        "MOVEMENT_SOURCE_RECORDED",
                        "MOVEMENT_TARGET_RECORDED"
                    )
                ),
                // YANKI
                Map.entry(
                    TransactionType.YANKI_PAYMENT, Set.of(
                        "YANKI_PAYMENT_OCCURRED",
                        "MOVEMENT_RECORDED"
                    )
                ),
                Map.entry(
                    TransactionType.YANKI_RECEIVE, Set.of(
                        "YANKI_RECEIVE_OCCURRED",
                        "MOVEMENT_RECORDED"
                    )
                ),
                Map.entry(
                    TransactionType.YANKI_LINK_DEBIT_CARD, Set.of(
                        "YANKI_LINK_DEBIT_CARD_OCCURRED"
                    )
                )
        );

    /**
     * Marca un evento como recibido y decide si completar la transacción.
     */
    public Completable markEventArrived(String transactionId, String eventType) {
        log.info("[TX-PROGRESS] Marking event {} for txId={}", eventType, transactionId);

        return progressRepository.updateProgress(transactionId, eventType)
                .andThen(progressRepository.findByTransactionId(transactionId))
                .flatMapCompletable(progress -> {
                    if (progress.failed()) {
                        log.warn("[TX-PROGRESS] Transaction FAILED txId={}", transactionId);
                        return updateStatusUseCase.markFailed(transactionId, "FAILED");
                    }

                    if (progress.isCompleted()) {
                        log.info("[TX-PROGRESS] Transaction COMPLETED txId={}", transactionId);
                        return updateStatusUseCase.markCompleted(transactionId, eventType);
                    }

                    Set<String> missing = progress.missingEvents();
                    log.info("[TX-PROGRESS] Pending txId={}, missingEvents={}", transactionId, missing);

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
