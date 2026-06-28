package pe.nom.charlygastelo.app.transactionservice.domain.model;

import java.math.BigDecimal;

public record Account(

        String id,

        String customerId,

        String number,

        AccountType type,

        BigDecimal balance,

        boolean active

) {

    public Account withBalance(BigDecimal balance) {
        return new Account(
                id,
                customerId,
                number,
                type,
                balance,
                active
        );
    }

}