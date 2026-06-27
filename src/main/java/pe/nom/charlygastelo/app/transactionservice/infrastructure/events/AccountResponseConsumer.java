package pe.nom.charlygastelo.app.transactionservice.infrastructure.events;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.shared.avro.dto.AccountResponseEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountResponseConsumer {

    private final AvroJsonDeserializer deserializer;
    private final AccountResponseRegistry registry;

    @KafkaListener(
            topics = "${topic.account-response}",
            groupId = "transaction-service")
    public void consume(String message) {

        AccountResponseEvent event =
                deserializer.deserialize(
                        message,
                        AccountResponseEvent.class,
                        AccountResponseEvent.getClassSchema());

        registry.complete(event);

    }

}