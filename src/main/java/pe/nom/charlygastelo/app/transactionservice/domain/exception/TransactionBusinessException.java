package pe.nom.charlygastelo.app.transactionservice.domain.exception;

public class TransactionBusinessException extends RuntimeException {
    public TransactionBusinessException(String message) {
        super(message);
    }
}