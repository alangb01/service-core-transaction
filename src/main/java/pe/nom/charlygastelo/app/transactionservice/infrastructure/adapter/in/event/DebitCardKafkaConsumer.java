package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.in.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.shared.avro.dto.DebitCardResponseEvent;
import pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.event.DebitCardResponseRegistry;

@Component
@Slf4j
@RequiredArgsConstructor
public class DebitCardKafkaConsumer {

    private final DebitCardResponseRegistry registry;

    @KafkaListener(
            topics = "${topic.debit-card-response}",
            groupId = "transaction-service-${random.uuid}"
    )
    public void listen(DebitCardResponseEvent event) {
        log.info("[KAFKA] Received DebitCardResponseEvent correlationId={}", event.getCorrelationId());

        try {
            registry.complete(event);
        } catch (Exception e) {
            log.error("[KAFKA] Error processing DebitCardResponseEvent", e);
        }
    }
}
