package pe.nom.charlygastelo.app.transactionservice.domain.model;


import java.math.BigDecimal;
import java.time.LocalDateTime;

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
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) { }
