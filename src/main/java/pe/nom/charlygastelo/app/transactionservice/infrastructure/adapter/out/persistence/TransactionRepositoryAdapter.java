package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.persistence;

import org.springframework.stereotype.Repository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.transactionservice.domain.model.Transaction;
import pe.nom.charlygastelo.app.transactionservice.domain.port.TransactionRepositoryPort;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {

    private final TransactionReactiveRepository repository;
    private final TransactionPersistenceMapper mapper;

    @Override
    public Single<Transaction> save(Transaction transaction) {
        return Single.fromPublisher(
                    repository.save(mapper.toDocument(transaction))
                ).map(mapper::toDomain)
                .doOnSuccess(saved ->
                        log.info("Transaction saved successfully. id={}, status={}",
                                saved.id(),
                                saved.status())
                )
                .doOnError(error ->
                        log.error("Error saving transaction. customer={}, error={}",
                                transaction.customerId(),
                                error.getMessage(),
                                error)
                );
    }

    @Override
    public Maybe<Transaction> findById(String id) {
        return Maybe.fromPublisher(repository.findById(id))
                .map(mapper::toDomain);
    }

    @Override
    public Flowable<Transaction> findAll() {
        return Flowable.fromPublisher(repository.findAll())
                .map(mapper::toDomain);
    }

    @Override
    public Flowable<Transaction> findByCustomerId(String customerId) {
        return Flowable.fromPublisher(repository.findByCustomerId(customerId))
                .map(mapper::toDomain);
    }

    @Override
    public Flowable<Transaction> findBySourceProductId(String productId) {
        return Flowable.fromPublisher(repository.findBySourceProductId(productId))
                .map(mapper::toDomain);
    }

    @Override
    public Flowable<Transaction> findByTargetProductId(String productId) {
        return Flowable.fromPublisher(repository.findByTargetProductId(productId))
                .map(mapper::toDomain);
    }

    @Override
    public Completable deleteById(String id) {
        return Completable.fromPublisher(repository.deleteById(id));
    }


}