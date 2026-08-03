package pe.nom.charlygastelo.app.transactionservice.domain.model;

public record DebitCard (
  String id,
  String accountId,
  String status
) { }
