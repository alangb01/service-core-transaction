package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.persistence;

import java.time.Instant;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.transactionservice.domain.model.TransactionProgress;
import pe.nom.charlygastelo.app.transactionservice.domain.port.TransactionProgressRepositoryPort;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionProgressRepositoryAdapter implements TransactionProgressRepositoryPort {

    private final ReactiveMongoTemplate mongoTemplate;
    private final TransactionProgressMongoRepository repository;
    private final TransactionProgressMapper mapper;

    @Override
    public Maybe<TransactionProgress> findByTransactionId(String transactionId) {
        return Maybe.fromPublisher(
                repository.findByTransactionId(transactionId)
                        .map(mapper::toDomain)
        );
    }

    @Override
    public Single<TransactionProgress> save(TransactionProgress progress) {
        return Single.fromPublisher(
                repository.save(mapper.toDocument(progress))
                        .map(mapper::toDomain)
        );
    }

    @Override
    public Completable updateProgress(String transactionId, String eventType) {
        log.info("[TX-PROGRESS] Updating progress for txId={}, eventType={}", transactionId, eventType);
        Update update = new Update()
                .addToSet("receivedEvents", eventType)   // agrega el evento al set
                .set("updatedAt", Instant.now());

        if (eventType.equals("FAILED")) {
            update.set("failed", true);
        }

        return Completable.fromPublisher(
                mongoTemplate.updateFirst(
                                Query.query(Criteria.where("transactionId").is(transactionId)),
                                update,
                                TransactionProgressDocument.class
                        )
                        .flatMap(result -> {
                            if (result.getModifiedCount() == 0) {
                                return Mono.error(new RuntimeException(
                                        "TransactionProgress not updated for txId=" + transactionId));
                            }
                            return Mono.empty();
                        })
        );
    }
}
