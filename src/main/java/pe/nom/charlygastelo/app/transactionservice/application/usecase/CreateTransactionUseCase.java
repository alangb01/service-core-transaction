package pe.nom.charlygastelo.app.transactionservice.application.usecase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.transactionservice.domain.exception.InvalidTransactionException;
import pe.nom.charlygastelo.app.transactionservice.domain.model.Transaction;
import pe.nom.charlygastelo.app.transactionservice.domain.model.TransactionStatus;
import pe.nom.charlygastelo.app.transactionservice.domain.port.AccountClientPort;
import pe.nom.charlygastelo.app.transactionservice.domain.port.CreditClientPort;
import pe.nom.charlygastelo.app.transactionservice.domain.port.TransactionEventProducerPort;
import pe.nom.charlygastelo.app.transactionservice.domain.port.TransactionRepositoryPort;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateTransactionUseCase {

    private final CreditClientPort creditClient;
    private final AccountClientPort accountClient;
    private final TransactionRepositoryPort repository;
    private final TransactionEventProducerPort producer;

    public Single<Transaction> execute(Transaction transaction, String token) {
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

        return validate(transaction, token)
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

    private Completable validate(Transaction transaction, String token) {

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

            case TRANSFER_TO_THIRD -> validateTransferToThird(transaction);
            case CREDIT_PAYMENT -> validateCreditPayment(transaction);

            case CREDIT_PAYMENT_THIRD -> null;
            case CREDIT_INTEREST -> null;
            case CREDIT_WITHDRAWL ->validateCreditWithdrawal(transaction, token);
            case CREDIT_CARD_CHARGE -> validateCreditCardCharge(transaction);

            case CREDIT_CARD_PAYMENT -> null;
            case CREDIT_CARD_INTEREST -> null;
            case DEBIT_CARD_PAYMENT -> validateDebitCardPayment(transaction);

            case DEBIT_CARD_PURCHASE -> null;
            case FIXED_TERM_DEPOSIT -> null;
            case FIXED_TERM_WITHDRAWAL -> null;
            case YANKI_PAYMENT -> validateYankiPayment(transaction);

            case YANKI_RECEIVE -> null;
            case YANKI_LINK_DEBIT_CARD -> null;
            case ACCOUNT_MAINTENANCE_FEE -> null;
            case TRANSACTION_FEE -> null;
        };
    }

    private Completable validateCreditWithdrawal(Transaction tx, String token) {

        // 1. Validar IDs obligatorios
        if (isBlank(tx.sourceProductId())) {
            return Completable.error(
                    new InvalidTransactionException("Credit id (sourceProductId) is required for credit withdrawals")
            );
        }

        if (isBlank(tx.targetProductId())) {
            return Completable.error(
                    new InvalidTransactionException("Destination account id (targetProductId) is required for credit withdrawals")
            );
        }

        if (tx.sourceProductType() == null) {
            return Completable.error(
                    new InvalidTransactionException("Source product type is required for credit withdrawals")
            );
        }

        if (tx.targetProductType() == null) {
            return Completable.error(
                    new InvalidTransactionException("Target product type is required for credit withdrawals")
            );
        }

        // 2. Validar monto
        if (tx.amount() == null || tx.amount().compareTo(BigDecimal.ZERO) <= 0) {
            return Completable.error(
                    new InvalidTransactionException("Withdrawal amount must be greater than zero")
            );
        }


        return Completable.complete();
    }

    private Completable validateCreditWithdrawalWithRest(Transaction tx, String token) {
        return creditClient.getCreditById(tx.sourceProductId(), token)
                .switchIfEmpty(Single.error(new InvalidTransactionException("Credit not found")))
                .flatMap(credit -> {

                    // 4. Validar pertenencia
                    if (!credit.customerId().equals(tx.customerId())) {
                        return Single.error(new InvalidTransactionException("Credit does not belong to this customer"));
                    }

                    // 5. Validar estado
                    if (!"ACTIVE".equals(credit.status())) {
                        return Single.error(new InvalidTransactionException("Credit is not active"));
                    }
//
//                    // 6. Validar deuda vencida
//                    if (credit.hasOverdueDebt()) {
//                        return Single.error(new InvalidTransactionException("Credit has overdue debt"));
//                    }
//
//                    // 7. Validar que permite retiros
//                    if (!credit.allowWithdrawal()) {
//                        return Single.error(new InvalidTransactionException("This credit does not allow withdrawals"));
//                    }

                    // 8. Validar disponibilidad
                    if (credit.balance().compareTo(tx.amount()) < 0) {
                        return Single.error(new InvalidTransactionException("Insufficient credit available"));
                    }

                    return Single.just(credit);
                })

                // 9. Validar cuenta destino
                .flatMap(credit ->
                        accountClient.getAccountById(tx.targetProductId(), token)
                                .switchIfEmpty(Single.error(new InvalidTransactionException("Destination account not found")))
                )
                .flatMapCompletable(account -> {

                    // 10. Validar pertenencia
                    if (!account.customerId().equals(tx.customerId())) {
                        return Completable.error(new InvalidTransactionException("Destination account does not belong to this customer"));
                    }

                    // 11. Validar estado
                    if (!"ACTIVE".equals(account.status())) {
                        return Completable.error(new InvalidTransactionException("Destination account is not active"));
                    }

                    return Completable.complete();
                });
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

    private Completable validateTransferToThird(Transaction tx) {

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