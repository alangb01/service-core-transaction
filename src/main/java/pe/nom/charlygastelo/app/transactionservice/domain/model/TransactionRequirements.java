package pe.nom.charlygastelo.app.transactionservice.domain.model;

import java.util.Set;

public final class TransactionRequirements {

    public static Set<String> forType(TransactionType type) {

        return switch (type) {

            // ------------------------------------------------------------
            // ACCOUNT
            // ------------------------------------------------------------
            case DEPOSIT -> Set.of(
                    "ACCOUNT_DEPOSIT_OCCURRED",
                    "MOVEMENT_TARGET_RECORDED"
            );

            case WITHDRAW -> Set.of(
                    "ACCOUNT_WITHDRAW_OCCURRED",
                    "MOVEMENT_SOURCE_RECORDED"
            );

            case TRANSFER, TRANSFER_TO_THIRD -> Set.of(
                    "ACCOUNT_WITHDRAW_OCCURRED",
                    "ACCOUNT_DEPOSIT_OCCURRED",
                    "MOVEMENT_SOURCE_RECORDED",
                    "MOVEMENT_TARGET_RECORDED"
            );

            case ACCOUNT_MAINTENANCE_FEE -> Set.of(
                    "ACCOUNT_MAINTENANCE_FEE_OCCURRED",
                    "MOVEMENT_SOURCE_RECORDED"
            );

            case TRANSACTION_FEE -> Set.of(
                    "TRANSACTION_FEE_OCCURRED",
                    "MOVEMENT_SOURCE_RECORDED"
            );

            // ------------------------------------------------------------
            // CREDIT
            // ------------------------------------------------------------
            case CREDIT_WITHDRAW -> Set.of(
                    "CREDIT_WITHDRAW_OCCURRED",
                    "ACCOUNT_DEPOSIT_OCCURRED",
                    "MOVEMENT_SOURCE_RECORDED",
                    "MOVEMENT_TARGET_RECORDED"
            );

            case CREDIT_PAYMENT, CREDIT_PAYMENT_THIRD -> Set.of(
                    "CREDIT_PAYMENT_OCCURRED",
                    "ACCOUNT_WITHDRAW_OCCURRED",
                    "MOVEMENT_SOURCE_RECORDED",
                    "MOVEMENT_TARGET_RECORDED"
            );

            case CREDIT_INTEREST -> Set.of(
                    "CREDIT_INTEREST_OCCURRED",
                    "MOVEMENT_SOURCE_RECORDED"
            );

            // ------------------------------------------------------------
            // CREDIT CARD
            // ------------------------------------------------------------
            case CREDIT_CARD_CHARGE -> Set.of(
                    "CREDIT_CARD_CHARGE_OCCURRED",
                    "MOVEMENT_SOURCE_RECORDED"
            );

            case CREDIT_CARD_PAYMENT -> Set.of(
                    "CREDIT_CARD_PAYMENT_OCCURRED",
                    "ACCOUNT_WITHDRAW_OCCURRED",
                    "MOVEMENT_SOURCE_RECORDED"
            );

            case CREDIT_CARD_INTEREST -> Set.of(
                    "CREDIT_CARD_INTEREST_OCCURRED",
                    "MOVEMENT_SOURCE_RECORDED"
            );

            // ------------------------------------------------------------
            // DEBIT CARD
            // ------------------------------------------------------------
            case DEBIT_CARD_PAYMENT -> Set.of(
                    "DEBIT_CARD_PAYMENT_OCCURRED",
                    "ACCOUNT_WITHDRAW_OCCURRED",
                    "MOVEMENT_SOURCE_RECORDED"
            );

            case DEBIT_CARD_PURCHASE -> Set.of(
                    "DEBIT_CARD_PURCHASE_OCCURRED",
                    "ACCOUNT_WITHDRAW_OCCURRED",
                    "MOVEMENT_SOURCE_RECORDED"
            );

            // ------------------------------------------------------------
            // YANKI
            // ------------------------------------------------------------
            case YANKI_SEND -> Set.of(
                    "DEBIT_CARD_ACCOUNTS_RESOLVED_RESPONSE",
                    "ACCOUNT_WITHDRAW_OCCURRED",
                    "MOVEMENT_SOURCE_RECORDED"
            );

            case YANKI_RECEIVE -> Set.of(
                    "DEBIT_CARD_ACCOUNTS_RESOLVED_RESPONSE",
                    "ACCOUNT_DEPOSIT_OCCURRED",
                    "MOVEMENT_TARGET_RECORDED"
            );

//            case YANKI_PAYMENT -> Set.of(
//                    "ACCOUNT_WITHDRAW_OCCURRED",
//                    "MOVEMENT_SOURCE_RECORDED"
//            );

            // ------------------------------------------------------------
            // FIXED TERM (NO SON TRANSACCIONES)
            // ------------------------------------------------------------
            case FIXED_TERM_DEPOSIT -> Set.of(
                    "FIXED_TERM_DEPOSIT_OCCURRED"
            );

            case FIXED_TERM_WITHDRAWAL -> Set.of(
                    "FIXED_TERM_WITHDRAWAL_OCCURRED"
            );

            // ------------------------------------------------------------
            // DEFAULT
            // ------------------------------------------------------------
            default -> Set.of();
        };
    }
}
