package pe.nom.charlygastelo.app.transactionservice.application.usecase;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import org.springframework.stereotype.Service;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.transactionservice.application.command.TransactionCommand;
import pe.nom.charlygastelo.app.transactionservice.application.common.EnumMapper;
import pe.nom.charlygastelo.app.transactionservice.domain.exception.InvalidTransactionException;
import pe.nom.charlygastelo.app.transactionservice.domain.model.*;
import pe.nom.charlygastelo.app.transactionservice.domain.port.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateTransactionUseCase {
    private final CreditClientPort creditClient;
    private final AccountClientPort accountClient;
    private final TransactionRepositoryPort repository;
    private final TransactionProgressRepositoryPort progressRepository;
    private final TransactionEventProducerPort producer;


    public Single<Transaction> execute(TransactionCommand cmd) {
        log.info("Preparing transaction for yanki. customer={}",
                cmd.customerId());

        Transaction tx = prepareTransaction(cmd);

        return Objects.requireNonNull(validate(tx))
                .doOnComplete(() ->
                    log.info("Transaction validation completed successfully. customer={}, type={}",
                            tx.customerId(),
                            tx.type())
                )
                .doOnError(error ->
                    log.error("Transaction validation failed. customer={}, error={}",
                            tx.customerId(),
                        error.getMessage(),
                        error)
                )
                .andThen(repository.save(tx))
                .flatMap(saved -> {
                    TransactionProgress progress = new TransactionProgress(
                            saved.id(),
                            saved.type(),
                            new HashSet<>(),                     // receivedEvents vacío
                            TransactionRequirements.forType(saved.type()), // requiredEvents según tipo
                            false,                               // failed
                            Instant.now()
                    );

                    return progressRepository.save(progress).ignoreElement().andThen(Single.just(saved));
                })
                .flatMap(saved ->
                        producer.publishTransactionCreated(saved)
                                .andThen(Single.just(saved))
                );
    }

    private Transaction prepareTransaction(TransactionCommand cmd) {
        ProductType sourceProductType = EnumMapper.safeValueOf(ProductType.class, cmd.sourceProductType());
        ProductType targetProductType = EnumMapper.safeValueOf(ProductType.class, cmd.targetProductType());
        TransactionType transactionType = EnumMapper.safeValueOf(TransactionType.class, cmd.type());

        return new Transaction(
                cmd.transactionId(),
                cmd.customerId(),
                cmd.sourceProductId(),
                cmd.targetProductId(),
                sourceProductType,
                targetProductType,
                transactionType,
                TransactionStatus.PENDING,
                cmd.amount(),
                cmd.commission(),
                cmd.description(),
                Instant.now(),
                null
        );
    }

    private Completable validate(Transaction transaction) {
        log.info("starting validation");
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
            //ACCOUNT
            case DEPOSIT -> validateDeposit(transaction);
            case WITHDRAW -> validateWithdrawal(transaction);
            case TRANSFER -> validateTransfer(transaction);
            case TRANSFER_TO_THIRD -> validateTransferToThird(transaction);

            //CREDIT
            case CREDIT_PAYMENT -> validateCreditPayment(transaction);
            case CREDIT_PAYMENT_THIRD -> null;
            case CREDIT_WITHDRAW -> validateCreditWithdrawal(transaction);
            case CREDIT_INTEREST -> null;

            //CREDIT CARD
            case CREDIT_CARD_CHARGE -> validateCreditCardCharge(transaction);
            case CREDIT_CARD_PAYMENT -> validateCreditCardPayment(transaction);
            case CREDIT_CARD_INTEREST -> null;

            //DEBIT CARD
            case DEBIT_CARD_PAYMENT -> validateDebitCardPayment(transaction);
            case DEBIT_CARD_PURCHASE -> validateDebitCardPurchase(transaction);

            case FIXED_TERM_DEPOSIT -> null;
            case FIXED_TERM_WITHDRAWAL -> null;

            //YANKI
            case YANKI_SEND -> validateYankiSend(transaction);

            case ACCOUNT_MAINTENANCE_FEE -> null;
            case TRANSACTION_FEE -> null;
        };
    }

    private Completable validateCreditWithdrawal(Transaction tx) {

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

        if (isBlank(tx.sourceProductId())) {
            return Completable.error(
                    new InvalidTransactionException("Credit card id is required for credit card charges")
            );
        }


        return Completable.complete();
    }

    private Completable validateCreditCardPayment(Transaction tx) {

        if (isBlank(tx.sourceProductId())) {
            return Completable.error(
                    new InvalidTransactionException("account id is required for credit card payment")
            );
        }

        if (tx.targetProductType() == null) {
            return Completable.error(
                    new InvalidTransactionException("credit card is required for credit card payment")
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

    private Completable validateDebitCardPurchase(Transaction tx) {

        if (isBlank(tx.sourceProductId())) {
            return Completable.error(
                    new InvalidTransactionException("Debit card id is required for debit card purchase")
            );
        }

        if (tx.sourceProductType() == null) {
            return Completable.error(
                    new InvalidTransactionException("Source product type is required for debit card purchase")
            );
        }

        return Completable.complete();
    }

    private Completable validateYankiReceive(Transaction tx) {
        log.info("starting validation receive");
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

    private Completable validateYankiSend(Transaction tx) {
        log.info("starting validation send");
        if (isBlank(tx.sourceProductId())) {
            return Completable.error(
                    new InvalidTransactionException("Source wallet id is required for Yanki payments")
            );
        }

        if (tx.sourceProductType() == null) {
            return Completable.error(
                    new InvalidTransactionException("Source product type is required for Yanki payments")
            );
        }

        if (tx.targetProductId().equals(ProductType.YANKI_WALLET.name())) {
            return validateYankiReceive(tx);
        }
        log.info("starting validation complete");
        return Completable.complete();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}