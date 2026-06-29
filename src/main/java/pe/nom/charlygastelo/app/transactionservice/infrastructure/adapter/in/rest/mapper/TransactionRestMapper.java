package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.in.rest.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import pe.nom.charlygastelo.app.transactionservice.domain.model.ProductType;
import pe.nom.charlygastelo.app.transactionservice.domain.model.Transaction;
import pe.nom.charlygastelo.app.transactionservice.domain.model.TransactionStatus;
import pe.nom.charlygastelo.app.transactionservice.domain.model.TransactionType;
import pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.in.rest.request.CreateTransactionRequest;
import pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.in.rest.response.TransactionResponse;

@Component
public class TransactionRestMapper {

    public Transaction toDomain(CreateTransactionRequest request) {
        return new Transaction(
                null,
                request.customerId(),
                request.sourceProductId(),
                request.targetProductId(),
                toProductType(request.sourceProductType()),
                toProductType(request.targetProductType()),
                TransactionType.valueOf(request.type()),
                TransactionStatus.PENDING,
                request.amount(),
                request.commission(),
                request.description(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.id(),
                transaction.customerId(),
                transaction.sourceProductId(),
                transaction.targetProductId(),
                transaction.sourceProductType() != null ? transaction.sourceProductType().name() : null,
                transaction.targetProductType() != null ? transaction.targetProductType().name() : null,
                transaction.type().name(),
                transaction.status().name(),
                transaction.amount(),
                transaction.commission(),
                transaction.description(),
                transaction.createdAt(),
                transaction.updatedAt()
        );
    }

    private ProductType toProductType(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return ProductType.valueOf(value);
    }
}