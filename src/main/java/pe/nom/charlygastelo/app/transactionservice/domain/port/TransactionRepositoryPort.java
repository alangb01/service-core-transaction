package pe.nom.charlygastelo.app.transactionservice.domain.port;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import pe.nom.charlygastelo.app.transactionservice.domain.model.Transaction;

public interface TransactionRepositoryPort {

    Single<Transaction> save(Transaction transaction);

    Maybe<Transaction> findById(String id);

    Flowable<Transaction> findAll();

    Flowable<Transaction> findByCustomerId(String customerId);

    Flowable<Transaction> findBySourceProductId(String productId);

    Flowable<Transaction> findByTargetProductId(String productId);

    Completable deleteById(String id);

}