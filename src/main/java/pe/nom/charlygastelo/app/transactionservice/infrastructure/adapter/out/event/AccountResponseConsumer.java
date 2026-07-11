package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.shared.avro.dto.AccountResponseEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountResponseConsumer {

    private final AccountResponseRegistry registry;

    @KafkaListener(
            topics = "${topic.account-response}",
            groupId = "transaction-service")
    public void consume(AccountResponseEvent event) {
        try {
            log.debug("AccountResponseEvent raw message received");

            log.info("AccountResponseEvent received. correlationId={}, found={}, accountId={}",
                    event.getCorrelationId(),
                    event.getFound(),
                    event.getAccountId());

            registry.complete(event);

        }
        catch (Exception e) {
            log.error("Error processing AccountResponseEvent", e);
        }
    }

}