package pe.nom.charlygastelo.app.transactionservice.infrastructure.client.dto;

import java.math.BigDecimal;

public record AccountResponse(
        String accountId,
        String customerId,
        BigDecimal balance,
        String currency,
        boolean active,
        String status
) {
}
