package pe.nom.charlygastelo.app.transactionservice.application.command;

import java.math.BigDecimal;

public record TransactionCommand (
        String transactionId,
        String customerId,
        String sourceProductId,
        String targetProductId,
        String sourceProductType,
        String targetProductType,
        String type,
        BigDecimal amount,
        BigDecimal commission,
        String description
) { }
