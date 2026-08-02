package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.in.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.shared.avro.dto.OverdueDebtResponseEvent;
import pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.event.OverdueDebtResponseRegistry;

@Component
@RequiredArgsConstructor
@Slf4j
public class OverdueDebtResponseConsumer {

    private final OverdueDebtResponseRegistry registry;

    @KafkaListener(topics = "${topic.overdue-debt-response}", groupId = "transaction-service")
    public void consume(OverdueDebtResponseEvent event) {
        try {


            registry.complete(event);

        }
        catch (Exception e) {
            log.error("Error processing OverdueDebtResponseEvent", e);
        }
    }
}