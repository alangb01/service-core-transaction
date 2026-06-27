package pe.nom.charlygastelo.app.transactionservice.infrastructure.events;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.shared.avro.dto.AccountRequestEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountRequestProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AvroJsonSerializer serializer;

    @Value("${topic.account-request}")
    private String accountRequestTopic;

    public void send(String correlationId, AccountRequestEvent event) {

        try {

            String payload = serializer.serialize(event);

            kafkaTemplate.send(
                    accountRequestTopic,
                    correlationId,
                    payload
            ).whenComplete((result, error) -> {

                if (error != null) {
                    log.error("Error sending AccountRequestEvent", error);
                }
                else {
                    log.info("AccountRequestEvent sent. correlationId={}", correlationId);
                }

            });

        }
        catch (Exception e) {
            log.error("Error serializing AccountRequestEvent", e);
        }

    }

}