package pe.nom.charlygastelo.app.transactionservice.infrastructure.events;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.shared.avro.dto.CreditResponseEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreditResponseConsumer {

    private final AvroJsonDeserializer deserializer;
    private final CreditResponseRegistry registry;

    @KafkaListener(
            topics = "${topic.credit-response}",
            groupId = "transaction-service")
    public void consume(String message) {

        CreditResponseEvent event =
                deserializer.deserialize(
                        message,
                        CreditResponseEvent.class,
                        CreditResponseEvent.getClassSchema());

        registry.complete(event);

    }

}