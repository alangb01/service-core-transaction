package pe.nom.charlygastelo.app.transactionservice.domain.model;

import java.math.BigDecimal;

public record Account(
        String id,
        String customerId,
        String number,
        String type,
        BigDecimal balance,
        boolean active
) {
}