package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.event;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.shared.avro.dto.DebitCardRequestEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class DebitCardRequestProducer {

    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;
    @Value("${topic.debit-card-request}")
    private String debitCardRequestTopic;

    public void send(String correlationId, DebitCardRequestEvent event) {
        try {

            kafkaTemplate.send(debitCardRequestTopic, correlationId, event)
                    .whenComplete((result, error) -> {
                        if (error != null) {
                            log.error("Error sending debitCardRequestEvent", error);
                        }
                        else {
                            log.info("debitCardRequestEvent sent. correlationId={}", correlationId);
                        }
                    });

        }
        catch (Exception e) {
            log.error("Error serializing debitCardRequestEvent", e);
        }
    }
}