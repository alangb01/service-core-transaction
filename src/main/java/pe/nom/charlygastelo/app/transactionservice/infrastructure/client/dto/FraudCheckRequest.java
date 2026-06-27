package pe.nom.charlygastelo.app.transactionservice.infrastructure.client.dto;

import java.math.BigDecimal;

public record FraudCheckRequest(
        String transactionId,
        String customerId,
        String accountId,
        BigDecimal amount
) {
}
