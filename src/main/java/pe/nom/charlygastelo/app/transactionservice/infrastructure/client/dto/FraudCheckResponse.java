package pe.nom.charlygastelo.app.transactionservice.infrastructure.client.dto;

public record FraudCheckResponse(
        boolean suspicious,
        String reason
) {
}
