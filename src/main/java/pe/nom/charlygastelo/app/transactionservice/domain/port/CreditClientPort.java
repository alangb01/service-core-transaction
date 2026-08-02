package pe.nom.charlygastelo.app.transactionservice.domain.port;

import io.reactivex.rxjava3.core.Maybe;
import pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.client.dto.CreditResponse;

public interface CreditClientPort {
    Maybe<CreditResponse> getCreditById(String creditId, String token);
}
