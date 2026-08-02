package pe.nom.charlygastelo.app.transactionservice.application.usecase;

import java.util.Set;
import org.springframework.stereotype.Service;
import io.reactivex.rxjava3.core.Completable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.transactionservice.domain.port.TransactionProgressRepositoryPort;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionProgressUseCase {
    private final TransactionProgressRepositoryPort progressRepository;
    private final UpdateTransactionStatusUseCase updateStatusUseCase;

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

}
