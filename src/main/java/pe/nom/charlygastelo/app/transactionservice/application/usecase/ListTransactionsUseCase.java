package pe.nom.charlygastelo.app.transactionservice.application.usecase;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.transactionservice.domain.model.Page;
import pe.nom.charlygastelo.app.transactionservice.domain.model.Transaction;
import pe.nom.charlygastelo.app.transactionservice.domain.port.TransactionRepositoryPort;

@RequiredArgsConstructor
@Slf4j
public class ListTransactionsUseCase {

    private final TransactionRepositoryPort repository;

    public Flowable<Transaction> all() {
        log.info("Listing all transactions");
        return repository.findAll();
    }

    public Single<Page<Transaction>> all(int page, int size) {
        log.info("Listing paginated transactions");
        return repository.findAll(page, size);
    }

    public Flowable<Transaction> byCustomer(String customerId) {
        log.info("Listing transactions by customer {}", customerId);
        return repository.findByCustomerId(customerId);
    }

    public Flowable<Transaction> byProduct(String productId) {
        log.info("Listing transactions by product {}", productId);
        return repository.findBySourceProductId(productId);
    }
}