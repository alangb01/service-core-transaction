package pe.nom.charlygastelo.app.transactionservice.infrastructure.events;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.shared.avro.dto.CreditRequestEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreditRequestProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AvroJsonSerializer serializer;

    @Value("${topic.credit-request}")
    private String creditRequestTopic;

    public void send(String correlationId, CreditRequestEvent event) {
        try {
            String payload = serializer.serialize(event);

            kafkaTemplate.send(creditRequestTopic, correlationId, payload)
                    .whenComplete((result, error) -> {
                        if (error != null) {
                            log.error("Error sending CreditRequestEvent", error);
                        }
                        else {
                            log.info("CreditRequestEvent sent. correlationId={}", correlationId);
                        }
                    });

        }
        catch (Exception e) {
            log.error("Error serializing CreditRequestEvent", e);
        }
    }
}