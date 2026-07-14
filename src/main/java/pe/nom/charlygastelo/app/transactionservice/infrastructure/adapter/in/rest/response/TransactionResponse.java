package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.in.rest.response;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponse(
        String id,
        String customerId,
        String sourceProductId,
        String targetProductId,
        String sourceProductType,
        String targetProductType,
        String type,
        String status,
        BigDecimal amount,
        BigDecimal commission,
        String description,
        Instant createdAt,
        Instant updatedAt
) {
}