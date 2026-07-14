package pe.nom.charlygastelo.app.transactionservice.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record Transaction(
        String id,
        String customerId,
        String sourceProductId,
        String targetProductId,
        ProductType sourceProductType,
        ProductType targetProductType,
        TransactionType type,
        TransactionStatus status,
        BigDecimal amount,
        BigDecimal commission,
        String description,
        Instant createdAt,
        Instant updatedAt
) {
    public Transaction withStatus(TransactionStatus newStatus) {
        return new Transaction(
                id,
                customerId,
                sourceProductId,
                targetProductId,
                sourceProductType,
                targetProductType,
                type,
                newStatus,
                amount,
                commission,
                description,
                createdAt,
                Instant.now()
        );
    }
}
