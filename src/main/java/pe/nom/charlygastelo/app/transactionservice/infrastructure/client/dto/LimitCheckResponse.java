package pe.nom.charlygastelo.app.transactionservice.infrastructure.client.dto;

public record LimitCheckResponse(
        boolean exceeded,
        String reason
) {
}
