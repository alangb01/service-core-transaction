package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.shared.avro.dto.AccountResponseEvent;
import pe.nom.charlygastelo.app.transactionservice.domain.exception.AccountNotFoundException;
import pe.nom.charlygastelo.app.transactionservice.domain.model.Account;
import pe.nom.charlygastelo.app.transactionservice.domain.model.AccountType;

@Component
@Slf4j
public class AccountResponseRegistry {

    private final Map<String, SingleEmitter<Account>> pendingRequests =
            new ConcurrentHashMap<>();

    public Single<Account> waitForResponse(String correlationId) {

        return Single.<Account>create(emitter ->
                        pendingRequests.put(correlationId, emitter)
                ).timeout(2, TimeUnit.SECONDS)
                .doFinally(() -> pendingRequests.remove(correlationId));

    }

    public void complete(AccountResponseEvent event) {

        String correlationId = event.getCorrelationId().toString();

        SingleEmitter<Account> emitter =
                pendingRequests.remove(correlationId);

        if (emitter == null) {
            return;
        }

        if (!event.getFound()) {
            emitter.onError(
                    new AccountNotFoundException("Account not found")
            );
            return;
        }

        Account account = new Account(
                event.getAccountId().toString(),
                event.getCustomerId().toString(),
                event.getNumber().toString(),
                AccountType.valueOf(event.getType().toString()),
                java.math.BigDecimal.valueOf(event.getBalance()),
                event.getActive()
        );

        emitter.onSuccess(account);

    }

}