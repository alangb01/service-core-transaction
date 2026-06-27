package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.in.rest.request;
import java.math.BigDecimal;

public record CreateTransactionRequest(
        String customerId,
        String sourceProductId,
        String targetProductId,
        String sourceProductType,
        String targetProductType,
        String type,
        BigDecimal amount,
        BigDecimal commission,
        String description
) {
}