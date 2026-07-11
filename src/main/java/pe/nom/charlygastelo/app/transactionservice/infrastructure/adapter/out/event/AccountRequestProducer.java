package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.event;

import org.apache.avro.specific.SpecificRecordBase;
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

    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;

    @Value("${topic.account-request}")
    private String accountRequestTopic;

    public void send(String correlationId, AccountRequestEvent event) {

        try {


            kafkaTemplate.send(
                    accountRequestTopic,
                    correlationId,
                    event
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