package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.persistence;

import org.springframework.stereotype.Component;
import pe.nom.charlygastelo.app.transactionservice.domain.model.Transaction;

@Component
public class TransactionPersistenceMapper {

    public TransactionDocument toDocument(Transaction domain) {
        return TransactionDocument.builder()
                .id(domain.id())
                .customerId(domain.customerId())
                .sourceProductId(domain.sourceProductId())
                .targetProductId(domain.targetProductId())
                .sourceProductType(domain.sourceProductType())
                .targetProductType(domain.targetProductType())
                .type(domain.type())
                .status(domain.status())
                .amount(domain.amount())
                .commission(domain.commission())
                .description(domain.description())
                .createdAt(domain.createdAt())
                .updatedAt(domain.updatedAt())
                .build();
    }

    public Transaction toDomain(TransactionDocument document) {
        return new Transaction(
                document.getId(),
                document.getCustomerId(),
                document.getSourceProductId(),
                document.getTargetProductId(),
                document.getSourceProductType(),
                document.getTargetProductType(),
                document.getType(),
                document.getStatus(),
                document.getAmount(),
                document.getCommission(),
                document.getDescription(),
                document.getCreatedAt(),
                document.getUpdatedAt()
        );
    }
}