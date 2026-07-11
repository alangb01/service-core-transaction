package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.event;

import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.shared.avro.dto.AccountRequestEvent;
import pe.nom.charlygastelo.app.transactionservice.domain.model.Account;
import pe.nom.charlygastelo.app.transactionservice.domain.port.AccountEventPort;

@Component
@RequiredArgsConstructor
public class AccountKafkaClient implements AccountEventPort {

    private final AccountRequestProducer producer;
    private final AccountResponseRegistry registry;

    @Override
    public Single<Account> getById(String accountId) {

        String correlationId = UUID.randomUUID().toString();

        AccountRequestEvent event =
                AccountRequestEvent.newBuilder()
                        .setEventId(UUID.randomUUID().toString())
                        .setEventType("ACCOUNT_REQUEST")
                        .setOccurredAt(Instant.now().toString())
                        .setVersion("1.0")
                        .setSource("transaction-service")
                        .setCorrelationId(correlationId)
                        .setAccountId(accountId)
                        .build();

        return registry
                .waitForResponse(correlationId)
                .doOnSubscribe(
                        d -> producer.send(correlationId, event)
                );

    }

}