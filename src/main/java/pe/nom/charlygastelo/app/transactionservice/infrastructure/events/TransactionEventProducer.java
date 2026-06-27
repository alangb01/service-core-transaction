package pe.nom.charlygastelo.app.transactionservice.infrastructure.events;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import io.reactivex.rxjava3.core.Completable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.transactionservice.domain.model.Transaction;
import pe.nom.charlygastelo.app.transactionservice.domain.port.TransactionEventProducerPort;
import pe.nom.charlygastelo.app.transactionservice.infrastructure.events.mapper.TransactionEventMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionEventProducer
        implements TransactionEventProducerPort {

    @Value("${topic.transaction-created}")
    private String transactionCreatedTopic;

    @Value("${topic.transaction-completed}")
    private String transactionCompletedTopic;

    @Value("${topic.transaction-failed}")
    private String transactionFailedTopic;

    @Value("${topic.transaction-deleted}")
    private String transactionDeletedTopic;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AvroJsonSerializer serializer;
    private final TransactionEventMapper mapper;

    @Override
    public Completable publishTransactionCreated(Transaction transaction) {
        return publish(
                transactionCreatedTopic,
                transaction.id(),
                mapper.toTransactionCreatedEvent(transaction)
        );
    }

    @Override
    public Completable publishTransactionCompleted(Transaction transaction) {
        return publish(
                transactionCompletedTopic,
                transaction.id(),
                mapper.toTransactionCompletedEvent(transaction)
        );
    }

    @Override
    public Completable publishTransactionFailed(Transaction transaction) {
        return publish(
                transactionFailedTopic,
                transaction.id(),
                mapper.toTransactionFailedEvent(transaction)
        );
    }

    @Override
    public Completable publishTransactionDeleted(Transaction transaction) {
        return publish(
                transactionDeletedTopic,
                transaction.id(),
                mapper.toTransactionDeletedEvent(transaction)
        );
    }

    private Completable publish(
            String topic,
            String key,
            SpecificRecordBase event) {

        return Completable.create(emitter -> {

            try {

                String payload = serializer.serialize(event);

                kafkaTemplate.send(topic, key, payload)
                        .whenComplete((result, error) -> {

                            if (error != null) {
                                emitter.onError(error);
                            }
                            else {
                                log.info("Transaction event published {}", topic);
                                emitter.onComplete();
                            }

                        });

            }
            catch (Exception e) {
                emitter.onError(e);
            }

        });

    }

}