package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.persistence.TransactionProgressDocument;
import reactor.core.publisher.Mono;

public interface TransactionProgressMongoRepository
        extends ReactiveMongoRepository<TransactionProgressDocument, String> {

    Mono<TransactionProgressDocument> findByTransactionId(String transactionId);
}
