package pe.nom.charlygastelo.app.transactionservice.domain.port;

import io.reactivex.rxjava3.core.Single;
import pe.nom.charlygastelo.app.transactionservice.domain.model.DebitCard;

public interface DebitCardRepositoryPort {
    Single<DebitCard> requestById(String debitCardId);

}
