package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.client.dto;

import java.math.BigDecimal;

public record CreditResponse (
        String id,
        String customerId,
        BigDecimal balance,
        String currency,
        boolean active,
        String status
) {


    public boolean hasOverdueDebt() {
        return false;
    }

    public boolean allowWithdrawal() {
        return false;
    }

}
