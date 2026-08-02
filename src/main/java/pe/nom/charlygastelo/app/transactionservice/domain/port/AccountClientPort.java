package pe.nom.charlygastelo.app.transactionservice.domain.port;

import io.reactivex.rxjava3.core.Maybe;
import pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.client.dto.AccountResponse;

public interface AccountClientPort {
    Maybe<AccountResponse> getAccountById(String accountId, String token);
}
