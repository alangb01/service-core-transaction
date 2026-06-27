package pe.nom.charlygastelo.app.transactionservice.infrastructure.events;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.transactionservice.infrastructure.events.mapper.TransactionEventMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionEventConsumer {

    private final TransactionEventMapper mapper;

    @KafkaListener (
            topics = "${topic.transaction-completed}",
            groupId = "transaction-service")
    public void completed(String message) {

        var event =
                mapper.toTransactionCompletedEvent(message);

        log.info("Transaction completed {}", event.getTransactionId());

    }

}