package pe.nom.charlygastelo.app.transactionservice.domain.model;

import java.time.Instant;
public record TransactionProgress(
        String transactionId,
        TransactionType type,
        boolean accountWithdrawOccurred,
        boolean accountDepositOccurred,
        boolean creditPaymentOccurred,
        boolean creditCardChargeOccurred,
        boolean movementRecorded,
        boolean failed,
        Instant updatedAt
) {

    public TransactionProgress mark(String eventType) {
        return switch (eventType) {
            case "ACCOUNT_WITHDRAW_OCCURRED" -> new TransactionProgress(
                    transactionId, type,
                    true, accountDepositOccurred, creditPaymentOccurred,
                    creditCardChargeOccurred, movementRecorded, failed, Instant.now()
            );
            case "ACCOUNT_DEPOSIT_OCCURRED" -> new TransactionProgress(
                    transactionId, type,
                    accountWithdrawOccurred, true, creditPaymentOccurred,
                    creditCardChargeOccurred, movementRecorded, failed, Instant.now()
            );
            case "CREDIT_PAYMENT_OCCURRED" -> new TransactionProgress(
                    transactionId, type,
                    accountWithdrawOccurred, accountDepositOccurred, true,
                    creditCardChargeOccurred, movementRecorded, failed, Instant.now()
            );
            case "CREDIT_CARD_CHARGE_OCCURRED" -> new TransactionProgress(
                    transactionId, type,
                    accountWithdrawOccurred, accountDepositOccurred, creditPaymentOccurred,
                    true, movementRecorded, failed, Instant.now()
            );
            case "MOVEMENT_RECORDED" -> new TransactionProgress(
                    transactionId, type,
                    accountWithdrawOccurred, accountDepositOccurred, creditPaymentOccurred,
                    creditCardChargeOccurred, true, failed, Instant.now()
            );
            case "FAILED" -> new TransactionProgress(
                    transactionId, type,
                    accountWithdrawOccurred, accountDepositOccurred, creditPaymentOccurred,
                    creditCardChargeOccurred, movementRecorded, true, Instant.now()
            );
            default -> this;
        };
    }

    public boolean hasEvent(String eventType) {
        return switch (eventType) {
            case "ACCOUNT_WITHDRAW_OCCURRED" -> accountWithdrawOccurred;
            case "ACCOUNT_DEPOSIT_OCCURRED" -> accountDepositOccurred;
            case "CREDIT_PAYMENT_OCCURRED" -> creditPaymentOccurred;
            case "CREDIT_CARD_CHARGE_OCCURRED" -> creditCardChargeOccurred;
            case "MOVEMENT_RECORDED" -> movementRecorded;
            default -> false;
        };
    }
}
