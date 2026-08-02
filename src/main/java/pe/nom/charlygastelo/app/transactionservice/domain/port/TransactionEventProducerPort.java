package pe.nom.charlygastelo.app.transactionservice.domain.port;

import io.reactivex.rxjava3.core.Completable;
import pe.nom.charlygastelo.app.transactionservice.domain.model.Transaction;

public interface TransactionEventProducerPort {

    Completable publishTransactionCreated(Transaction transaction);

    Completable publishTransactionDeleted(Transaction transaction);

    Completable publishTransactionUpdated(Transaction transaction);

    Completable publishTransactionCompleted(String transactionId);

    Completable publishTransactionFailed(String transactionId, String reason);

}