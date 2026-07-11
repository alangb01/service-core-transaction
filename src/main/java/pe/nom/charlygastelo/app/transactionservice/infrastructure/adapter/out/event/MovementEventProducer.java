package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.event;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.core.Completable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.transactionservice.domain.model.Transaction;
import pe.nom.charlygastelo.app.transactionservice.domain.port.MovementEventProducerPort;
import pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.event.mapper.MovementEventMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class MovementEventProducer implements MovementEventProducerPort {


    @Value("${topic.movement-register-request}")
    private String movementCreatedTopic;

    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;
    private final MovementEventMapper movementMapper;

    @Override
    public Completable publishMovementDebitCreated(Transaction transaction) {
        SpecificRecordBase event = movementMapper.toMovementDebitCreatedEvent(transaction);
        return publish(
                movementCreatedTopic,
                transaction.id(),
                event
            );
    }

    @Override
    public Completable publishMovementCreditCreated(Transaction transaction) {
        SpecificRecordBase event = movementMapper.toMovementCreditCreatedEvent(transaction);
        return publish(
                movementCreatedTopic,
                transaction.id(),
                event
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
                                log.info("Movement event published {}", topic);
                                emitter.onComplete();
                            }

                        });

            }
            catch (Exception e) {
                emitter.onError(e);
            }

        })
        .doOnComplete(() ->
            log.info("MovementCreatedEvent published successfully. transactionId={}", key)
        )
        .doOnError(error ->
            log.error("Error publishing MovementCreatedEvent. transactionId={}, error={}", key,
                        error.getMessage(),
                        error)
        );

    }

}