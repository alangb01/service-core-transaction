package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Flux;

public interface TransactionReactiveRepository
        extends ReactiveMongoRepository<TransactionDocument, String> {

    Flux<TransactionDocument> findByCustomerId(String customerId);

    Flux<TransactionDocument> findBySourceProductId(String productId);

    Flux<TransactionDocument> findByTargetProductId(String productId);
}