package pe.nom.charlygastelo.app.transactionservice.infrastructure.events;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.shared.avro.dto.OverdueDebtRequestEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class OverdueDebtRequestProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AvroJsonSerializer serializer;

    @Value("${topic.overdue-debt-request}")
    private String overdueDebtRequestTopic;

    public void send(String correlationId, OverdueDebtRequestEvent event) {
        try {
            String payload = serializer.serialize(event);

            kafkaTemplate.send(overdueDebtRequestTopic, correlationId, payload)
                    .whenComplete((result, error) -> {
                        if (error != null) {
                            log.error("Error sending OverdueDebtRequestEvent", error);
                        }
                        else {
                            log.info("OverdueDebtRequestEvent sent. correlationId={}", correlationId);
                        }
                    });

        }
        catch (Exception e) {
            log.error("Error serializing OverdueDebtRequestEvent", e);
        }
    }
}