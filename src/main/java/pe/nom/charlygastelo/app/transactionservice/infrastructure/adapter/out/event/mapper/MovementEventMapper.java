package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.event.mapper;

import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;
import pe.nom.charlygastelo.app.shared.avro.dto.MovementCreateRequestEvent;
import pe.nom.charlygastelo.app.transactionservice.domain.model.Transaction;

@Component
public class MovementEventMapper {

    public MovementCreateRequestEvent toMovementDebitCreatedEvent(Transaction transaction) {
        String productId = value(transaction.sourceProductId());
        String productType = value(transaction.sourceProductType().name());

        return MovementCreateRequestEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("MOVEMENT_CREATE_REQUEST")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("transaction-service")
                .setCustomerId(value(transaction.customerId()))
                .setProductId(productId)
                .setProductType(productType)
                .setMovementType("DEBIT")
                .setAmount(transaction.amount().doubleValue())
                .setBalanceAfter(0.0)
                .setDescription(value(transaction.description()))
                .setTransactionId(transaction.id())
                .build();
    }

    public MovementCreateRequestEvent toMovementCreditCreatedEvent(Transaction transaction) {
        String productId = value(transaction.targetProductId());
        String productType = value(transaction.targetProductType().name());

        return MovementCreateRequestEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("MOVEMENT_CREATE_REQUEST")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("transaction-service")
                .setCustomerId(value(transaction.customerId()))
                .setProductId(productId)
                .setProductType(productType)
                .setMovementType("CREDIT")
                .setAmount(transaction.amount().doubleValue())
                .setBalanceAfter(0.0)
                .setDescription(value(transaction.description()))
                .setTransactionId(transaction.id())
                .build();
    }


    private String value(String value) {
        return value == null ? "" : value;
    }
}