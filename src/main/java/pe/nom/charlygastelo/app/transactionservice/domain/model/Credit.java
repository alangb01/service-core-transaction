package pe.nom.charlygastelo.app.transactionservice.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record Credit(

        String id,

        String customerId,

        String number,

        CreditType type,

        CreditStatus status,

        BigDecimal creditLimit,

        BigDecimal balance,

        BigDecimal availableBalance,

        BigDecimal interestRate,

        Integer installments,

        LocalDate dueDate,

        boolean overdue,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {

    public Credit withBalance(BigDecimal balance) {
        return new Credit(
                id,
                customerId,
                number,
                type,
                status,
                creditLimit,
                balance,
                creditLimit.subtract(balance),
                interestRate,
                installments,
                dueDate,
                overdue,
                createdAt,
                LocalDateTime.now()
        );
    }

    public Credit withAvailableBalance(BigDecimal availableBalance) {
        return new Credit(
                id,
                customerId,
                number,
                type,
                status,
                creditLimit,
                balance,
                availableBalance,
                interestRate,
                installments,
                dueDate,
                overdue,
                createdAt,
                LocalDateTime.now()
        );
    }

    public Credit withStatus(CreditStatus status) {
        return new Credit(
                id,
                customerId,
                number,
                type,
                status,
                creditLimit,
                balance,
                availableBalance,
                interestRate,
                installments,
                dueDate,
                overdue,
                createdAt,
                LocalDateTime.now()
        );
    }

    public Credit withOverdue(boolean overdue) {
        return new Credit(
                id,
                customerId,
                number,
                type,
                status,
                creditLimit,
                balance,
                availableBalance,
                interestRate,
                installments,
                dueDate,
                overdue,
                createdAt,
                LocalDateTime.now()
        );
    }
}