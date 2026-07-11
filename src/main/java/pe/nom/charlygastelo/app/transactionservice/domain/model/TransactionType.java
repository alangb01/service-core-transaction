package pe.nom.charlygastelo.app.transactionservice.domain.model;

public enum TransactionType {
//    DEPOSIT,
//    WITHDRAWAL,
//    TRANSFER,
//    CREDIT_PAYMENT,
//    CREDIT_CARD_CHARGE,
//    DEBIT_CARD_PAYMENT,
//    YANKI_PAYMENT

    // Movimientos básicos
    DEPOSIT,
    WITHDRAWAL,

    // Transferencias
    TRANSFER,
    TRANSFER_TO_THIRD,

    // Créditos
    CREDIT_WITHDRAWL,
    CREDIT_PAYMENT,
    CREDIT_PAYMENT_THIRD,
    CREDIT_INTEREST,

    // Tarjetas de crédito
    CREDIT_CARD_CHARGE,
    CREDIT_CARD_PAYMENT,
    CREDIT_CARD_INTEREST,

    // Tarjetas de débito
    DEBIT_CARD_PAYMENT,
    DEBIT_CARD_PURCHASE,

    // Plazo fijo
    FIXED_TERM_DEPOSIT,
    FIXED_TERM_WITHDRAWAL,

    // Yanki
    YANKI_PAYMENT,
    YANKI_RECEIVE,
    YANKI_LINK_DEBIT_CARD,

    // Comisiones
    ACCOUNT_MAINTENANCE_FEE,
    TRANSACTION_FEE
}