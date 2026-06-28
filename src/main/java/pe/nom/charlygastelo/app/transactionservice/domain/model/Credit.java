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

    public Credit withPayment(BigDecimal amount) {
        BigDecimal newBalance = balance.subtract(amount);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Payment exceeds current balance");
        }

        return new Credit(
                id,
                customerId,
                number,
                type,
                status,
                creditLimit,
                newBalance,
                availableBalance.add(amount),
                interestRate,
                installments,
                dueDate,
                overdue,
                createdAt,
                LocalDateTime.now()
        );
    }

    public Credit withCharge(BigDecimal amount) {
        BigDecimal newAvailableBalance = availableBalance.subtract(amount);

        if (newAvailableBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Insufficient available credit");
        }

        return new Credit(
                id,
                customerId,
                number,
                type,
                status,
                creditLimit,
                balance.add(amount),
                newAvailableBalance,
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