package pe.nom.charlygastelo.app.transactionservice.domain.port;

import io.reactivex.rxjava3.core.Completable;
import pe.nom.charlygastelo.app.transactionservice.domain.model.Transaction;

public interface TransactionEventProducerPort {

    Completable publishTransactionCreated(Transaction transaction);

    Completable publishTransactionCompleted(Transaction transaction);

    Completable publishTransactionFailed(Transaction transaction);

    Completable publishTransactionDeleted(Transaction transaction);
}