package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.event;

import org.apache.avro.specific.SpecificRecordBase;
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

    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;
    @Value("${topic.credit-request}")
    private String creditRequestTopic;

    public void send(String correlationId, CreditRequestEvent event) {
        try {

            kafkaTemplate.send(creditRequestTopic, correlationId, event)
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