package pe.nom.charlygastelo.app.transactionservice.domain.port;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import org.springframework.beans.factory.support.InstanceSupplier;
import pe.nom.charlygastelo.app.transactionservice.domain.model.TransactionProgress;

public interface TransactionProgressRepositoryPort {

    Maybe<TransactionProgress> findByTransactionId(String transactionId);

    Single<TransactionProgress> save(TransactionProgress progress);

    Completable updateProgress(String transactionId, String eventType);
}
