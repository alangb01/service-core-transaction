package pe.nom.charlygastelo.app.transactionservice.domain.port;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.client.dto.AccountResponse;
import reactor.core.publisher.Mono;

public interface AccountClientPort {
    Maybe<AccountResponse> getAccountById(String accountId, String token);
}
