package pe.nom.charlygastelo.app.transactionservice.domain.port;

import io.reactivex.rxjava3.core.Completable;
import pe.nom.charlygastelo.app.transactionservice.domain.model.Transaction;

public interface TransactionManagementEventProducerPort {

    Completable publishTransactionCreated(Transaction transaction);

    Completable publishTransactionDeleted(Transaction transaction);

}