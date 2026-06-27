package pe.nom.charlygastelo.app.transactionservice.domain.port;

import io.reactivex.rxjava3.core.Single;
import pe.nom.charlygastelo.app.transactionservice.domain.model.Credit;

public interface CreditEventPort {

    /**
     * Obtiene un crédito por su identificador.
     */
    Single<Credit> getById(String creditId);

    /**
     * Verifica si el cliente posee deuda vencida.
     */
    Single<Boolean> hasOverdueDebt(String customerId);


}