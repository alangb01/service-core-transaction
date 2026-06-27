package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.persistence;

import org.springframework.stereotype.Repository;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.transactionservice.domain.model.Transaction;
import pe.nom.charlygastelo.app.transactionservice.domain.port.TransactionRepositoryPort;
import reactor.adapter.rxjava.RxJava3Adapter;

@Repository
@RequiredArgsConstructor
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {

    private final TransactionReactiveRepository repository;
    private final TransactionPersistenceMapper mapper;

    @Override
    public Single<Transaction> save(Transaction transaction) {
        return RxJava3Adapter.monoToSingle(
                repository.save(mapper.toDocument(transaction))
        ).map(mapper::toDomain);
    }

    @Override
    public Maybe<Transaction> findById(String id) {
        return RxJava3Adapter.monoToMaybe(repository.findById(id))
                .map(mapper::toDomain);
    }

    @Override
    public Flowable<Transaction> findAll() {
        return RxJava3Adapter.fluxToFlowable(repository.findAll())
                .map(mapper::toDomain);
    }

    @Override
    public Flowable<Transaction> findByCustomerId(String customerId) {
        return RxJava3Adapter.fluxToFlowable(repository.findByCustomerId(customerId))
                .map(mapper::toDomain);
    }

    @Override
    public Flowable<Transaction> findBySourceProductId(String productId) {
        return RxJava3Adapter.fluxToFlowable(repository.findBySourceProductId(productId))
                .map(mapper::toDomain);
    }

    @Override
    public Flowable<Transaction> findByTargetProductId(String productId) {
        return RxJava3Adapter.fluxToFlowable(repository.findByTargetProductId(productId))
                .map(mapper::toDomain);
    }

    @Override
    public Completable deleteById(String id) {
        return RxJava3Adapter.monoToCompletable(repository.deleteById(id));
    }
}