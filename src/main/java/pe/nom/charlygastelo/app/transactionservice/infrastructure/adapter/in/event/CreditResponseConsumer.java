package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.in.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.shared.avro.dto.CreditResponseEvent;
import pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.event.CreditResponseRegistry;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreditResponseConsumer {

    private final CreditResponseRegistry registry;

    @KafkaListener(
            topics = "${topic.credit-response}",
            groupId = "transaction-service")
    public void consume(CreditResponseEvent event) {
        try {
            log.debug("CreditResponseEvent raw message received");


            log.info("CreditResponseEvent received. correlationId={}, found={}, creditId={}",
                    event.getCorrelationId(),
                    event.getFound(),
                    event.getCreditId());

            registry.complete(event);

        }
        catch (Exception e) {
            log.error("Error processing CreditResponseEvent", e);
        }
    }

}