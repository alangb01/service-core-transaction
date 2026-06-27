package pe.nom.charlygastelo.app.transactionservice.application.usecase;

import io.reactivex.rxjava3.core.Maybe;
import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.transactionservice.domain.exception.TransactionNotFoundException;
import pe.nom.charlygastelo.app.transactionservice.domain.model.Transaction;
import pe.nom.charlygastelo.app.transactionservice.domain.port.TransactionRepositoryPort;

@RequiredArgsConstructor
public class GetTransactionUseCase {

    private final TransactionRepositoryPort repository;

    public Maybe<Transaction> byId(String id) {
        return repository.findById(id)
                .switchIfEmpty(Maybe.error(
                        new TransactionNotFoundException("Transaction not found: " + id)
                ));
    }
}