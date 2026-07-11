package pe.nom.charlygastelo.app.transactionservice.domain.port;

import io.reactivex.rxjava3.core.Completable;
import pe.nom.charlygastelo.app.transactionservice.domain.model.Transaction;

public interface MovementEventProducerPort {

    Completable publishMovementCreditCreated(Transaction transaction);

    Completable publishMovementDebitCreated(Transaction transaction);
}