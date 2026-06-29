package pe.nom.charlygastelo.app.transactionservice.application.usecase;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.transactionservice.domain.exception.InvalidTransactionException;
import pe.nom.charlygastelo.app.transactionservice.domain.model.Transaction;
import pe.nom.charlygastelo.app.transactionservice.domain.model.TransactionStatus;
import pe.nom.charlygastelo.app.transactionservice.domain.port.TransactionEventProducerPort;
import pe.nom.charlygastelo.app.transactionservice.domain.port.TransactionRepositoryPort;

@RequiredArgsConstructor
@Slf4j
public class CreateTransactionUseCase {

    private final TransactionRepositoryPort repository;
    private final TransactionEventProducerPort producer;

    public Single<Transaction> execute(Transaction transaction) {
        log.info("Creating transaction. customer={}, type={}",
                transaction.customerId(), transaction.type());

        Transaction pending = new Transaction(
                transaction.id(),
                transaction.customerId(),
                transaction.sourceProductId(),
                transaction.targetProductId(),
                transaction.sourceProductType(),
                transaction.targetProductType(),
                transaction.type(),
                TransactionStatus.PENDING,
                transaction.amount(),
                transaction.commission(),
                transaction.description(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        return validate(transaction)
                .doOnComplete(() ->
                        log.info("Transaction validation completed successfully. customer={}, type={}",
                                transaction.customerId(),
                                transaction.type())
                )
                .doOnError(error ->
                        log.error("Transaction validation failed. customer={}, error={}",
                                transaction.customerId(),
                                error.getMessage(),
                                error)
                )
                .andThen(repository.save(pending))
                .doOnSuccess(saved ->
                        log.info("Transaction saved successfully. id={}, status={}",
                                saved.id(),
                                saved.status())
                )
                .doOnError(error ->
                        log.error("Error saving transaction. customer={}, error={}",
                                transaction.customerId(),
                                error.getMessage(),
                                error)
                )
                .flatMap(saved ->
                        producer.publishTransactionCreated(saved)
                                .doOnComplete(() ->
                                        log.info("TransactionCreatedEvent published successfully. transactionId={}",
                                                saved.id())
                                )
                                .doOnError(error ->
                                        log.error("Error publishing TransactionCreatedEvent. transactionId={}, error={}",
                                                saved.id(),
                                                error.getMessage(),
                                                error)
                                )
                                .andThen(Single.just(saved))
                )
                .doOnSuccess(saved ->
                        log.info("Transaction process finished successfully. transactionId={}",
                                saved.id())
                )
                .doOnError(error ->
                        log.error("Transaction process failed. customer={}, error={}",
                                transaction.customerId(),
                                error.getMessage(),
                                error)
                );
    }

//    private Completable validate(Transaction transaction) {
//
//        if (transaction == null) {
//            return Completable.error(
//                    new InvalidTransactionException("Transaction cannot be null")
//            );
//        }
//
//        if (isBlank(transaction.customerId())) {
//            return Completable.error(
//                    new InvalidTransactionException("Customer id is required")
//            );
//        }
//
//        if (transaction.type() == null) {
//            return Completable.error(
//                    new InvalidTransactionException("Transaction type is required")
//            );
//        }
//
//        if (transaction.amount() == null
//                || transaction.amount().compareTo(BigDecimal.ZERO) <= 0) {
//
//            return Completable.error(
//                    new InvalidTransactionException("Amount must be greater than zero")
//            );
//        }
//
//        if (transaction.type() != TransactionType.DEPOSIT) {
//
//            if (transaction.sourceProductType() == null) {
//                return Completable.error(
//                        new InvalidTransactionException("Source product type is required")
//                );
//            }
//
//            if (isBlank(transaction.sourceProductId())) {
//                return Completable.error(
//                        new InvalidTransactionException("Source product id is required")
//                );
//            }
//        }
//
//
//        switch (transaction.type()) {
//
//            case TRANSFER -> {
//                if (isBlank(transaction.targetProductId())) {
//                    return Completable.error(
//                            new InvalidTransactionException("Target product id is required for transfers")
//                    );
//                }
//
//                if (transaction.targetProductType() == null) {
//                    return Completable.error(
//                            new InvalidTransactionException("Target product type is required for transfers")
//                    );
//                }
//
//                if (transaction.sourceProductId().equals(transaction.targetProductId())) {
//                    return Completable.error(
//                            new InvalidTransactionException("Source and target products cannot be the same")
//                    );
//                }
//            }
//
//            case CREDIT_PAYMENT -> {
//                if (isBlank(transaction.targetProductId())) {
//                    return Completable.error(
//                            new InvalidTransactionException("Credit id is required for credit payments")
//                    );
//                }
//            }
//
//            case CREDIT_CARD_CHARGE, DEBIT_CARD_PAYMENT, YANKI_PAYMENT -> {
//                if (isBlank(transaction.targetProductId())) {
//                    return Completable.error(
//                            new InvalidTransactionException("Target product id is required")
//                    );
//                }
//            }
//
//            default -> {
//                // DEPOSIT / WITHDRAWAL pueden usar solo sourceProductId.
//            }
//        }
//
//        return Completable.complete();
//    }

    private Completable validate(Transaction transaction) {

        if (transaction == null) {
            return Completable.error(
                    new InvalidTransactionException("Transaction cannot be null")
            );
        }

        if (isBlank(transaction.customerId())) {
            return Completable.error(
                    new InvalidTransactionException("Customer id is required")
            );
        }

        if (transaction.type() == null) {
            return Completable.error(
                    new InvalidTransactionException("Transaction type is required")
            );
        }

        if (transaction.amount() == null
                || transaction.amount().compareTo(BigDecimal.ZERO) <= 0) {

            return Completable.error(
                    new InvalidTransactionException("Amount must be greater than zero")
            );
        }

        return switch (transaction.type()) {

            case DEPOSIT -> validateDeposit(transaction);

            case WITHDRAWAL -> validateWithdrawal(transaction);

            case TRANSFER -> validateTransfer(transaction);

            case CREDIT_PAYMENT -> validateCreditPayment(transaction);

            case CREDIT_CARD_CHARGE -> validateCreditCardCharge(transaction);

            case DEBIT_CARD_PAYMENT -> validateDebitCardPayment(transaction);

            case YANKI_PAYMENT -> validateYankiPayment(transaction);
        };
    }

    private Completable validateDeposit(Transaction tx) {

        if (isBlank(tx.targetProductId())) {
            return Completable.error(
                    new InvalidTransactionException("Target account id is required for deposits")
            );
        }

        if (tx.targetProductType() == null) {
            return Completable.error(
                    new InvalidTransactionException("Target product type is required for deposits")
            );
        }

        return Completable.complete();
    }

    private Completable validateWithdrawal(Transaction tx) {

        if (isBlank(tx.sourceProductId())) {
            return Completable.error(
                    new InvalidTransactionException("Source account id is required for withdrawals")
            );
        }

        if (tx.sourceProductType() == null) {
            return Completable.error(
                    new InvalidTransactionException("Source product type is required for withdrawals")
            );
        }

        return Completable.complete();
    }

    private Completable validateTransfer(Transaction tx) {

        if (isBlank(tx.sourceProductId())) {
            return Completable.error(
                    new InvalidTransactionException("Source product id is required for transfers")
            );
        }

        if (tx.sourceProductType() == null) {
            return Completable.error(
                    new InvalidTransactionException("Source product type is required for transfers")
            );
        }

        if (isBlank(tx.targetProductId())) {
            return Completable.error(
                    new InvalidTransactionException("Target product id is required for transfers")
            );
        }

        if (tx.targetProductType() == null) {
            return Completable.error(
                    new InvalidTransactionException("Target product type is required for transfers")
            );
        }

        if (tx.sourceProductId().equals(tx.targetProductId())) {
            return Completable.error(
                    new InvalidTransactionException("Source and target products cannot be the same")
            );
        }

        return Completable.complete();
    }

    private Completable validateCreditPayment(Transaction tx) {

        if (isBlank(tx.sourceProductId())) {
            return Completable.error(
                    new InvalidTransactionException("Source account id is required for credit payments")
            );
        }

        if (tx.sourceProductType() == null) {
            return Completable.error(
                    new InvalidTransactionException("Source product type is required for credit payments")
            );
        }

        if (isBlank(tx.targetProductId())) {
            return Completable.error(
                    new InvalidTransactionException("Credit id is required for credit payments")
            );
        }

        if (tx.targetProductType() == null) {
            return Completable.error(
                    new InvalidTransactionException("Target product type is required for credit payments")
            );
        }

        return Completable.complete();
    }

    private Completable validateCreditCardCharge(Transaction tx) {

        if (isBlank(tx.targetProductId())) {
            return Completable.error(
                    new InvalidTransactionException("Credit card id is required for credit card charges")
            );
        }

        if (tx.targetProductType() == null) {
            return Completable.error(
                    new InvalidTransactionException("Target product type is required for credit card charges")
            );
        }

        return Completable.complete();
    }

    private Completable validateDebitCardPayment(Transaction tx) {

        if (isBlank(tx.sourceProductId())) {
            return Completable.error(
                    new InvalidTransactionException("Debit card id is required for debit card payments")
            );
        }

        if (tx.sourceProductType() == null) {
            return Completable.error(
                    new InvalidTransactionException("Source product type is required for debit card payments")
            );
        }

        return Completable.complete();
    }

    private Completable validateYankiPayment(Transaction tx) {

        if (isBlank(tx.targetProductId())) {
            return Completable.error(
                    new InvalidTransactionException("Target wallet id is required for Yanki payments")
            );
        }

        if (tx.targetProductType() == null) {
            return Completable.error(
                    new InvalidTransactionException("Target product type is required for Yanki payments")
            );
        }

        return Completable.complete();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}