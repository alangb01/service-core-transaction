package pe.nom.charlygastelo.app.transactionservice.domain.exception;

public class OverdueDebtException extends RuntimeException {
    public OverdueDebtException(String message) {
        super(message);
    }
}