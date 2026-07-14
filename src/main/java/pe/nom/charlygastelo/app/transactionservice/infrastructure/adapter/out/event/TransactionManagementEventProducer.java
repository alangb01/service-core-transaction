package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.event;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.core.Completable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.transactionservice.domain.model.Transaction;
import pe.nom.charlygastelo.app.transactionservice.domain.port.TransactionManagementEventProducerPort;
import pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.event.mapper.TransactionEventMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionManagementEventProducer implements TransactionManagementEventProducerPort {

    @Value("${topic.transaction-created}")
    private String transactionCreatedTopic;



    @Value("${topic.transaction-deleted}")
    private String transactionDeletedTopic;

    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;
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


                kafkaTemplate.send(topic, key, event)
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

        })
        .doOnComplete(() ->
            log.info("TransactionCreatedEvent published successfully. transactionId={}", key)
        )
        .doOnError(error ->
            log.error("Error publishing TransactionCreatedEvent. transactionId={}, error={}", key,
                        error.getMessage(),
                        error)
        );

    }

}