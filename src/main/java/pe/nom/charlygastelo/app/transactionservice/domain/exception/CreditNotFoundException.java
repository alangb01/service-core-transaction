package pe.nom.charlygastelo.app.transactionservice.domain.exception;

public class CreditNotFoundException extends RuntimeException {
    public CreditNotFoundException(String message) {
        super(message);
    }
}
