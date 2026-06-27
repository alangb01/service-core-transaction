package pe.nom.charlygastelo.app.transactionservice.infrastructure.client.dto;

import java.math.BigDecimal;

public record LimitCheckRequest(
        String customerId,
        String accountId,
        BigDecimal amount
) {
}
