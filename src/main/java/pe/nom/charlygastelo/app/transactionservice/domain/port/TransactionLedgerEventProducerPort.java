package pe.nom.charlygastelo.app.transactionservice.domain.port;

import io.reactivex.rxjava3.core.Completable;

public interface TransactionLedgerEventProducerPort {

    Completable publishTransactionCompleted(String transactionId);

    Completable publishTransactionFailed(String transactionId, String reason);

}