package pe.nom.charlygastelo.app.transactionservice.application.command;

import java.math.BigDecimal;

public record TransactionYankiCommand(
        String transactionId,
        String customerId,
        String sourceProductId,
        String targetProductId,
        BigDecimal amount,
        BigDecimal commission,
        String description
) { }
