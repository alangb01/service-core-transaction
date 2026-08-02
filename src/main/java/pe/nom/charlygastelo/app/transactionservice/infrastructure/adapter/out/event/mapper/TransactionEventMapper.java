package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.event.mapper;

import java.time.Instant;
import java.util.UUID;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import pe.nom.charlygastelo.app.shared.avro.dto.TransactionCompletedEvent;
import pe.nom.charlygastelo.app.shared.avro.dto.TransactionCreatedEvent;
import pe.nom.charlygastelo.app.shared.avro.dto.TransactionDeletedEvent;
import pe.nom.charlygastelo.app.shared.avro.dto.TransactionFailedEvent;
import pe.nom.charlygastelo.app.transactionservice.domain.model.Transaction;

@Component
public class TransactionEventMapper {

    public TransactionCreatedEvent toTransactionCreatedEvent(Transaction transaction) {

        return TransactionCreatedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("TRANSACTION_CREATED")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("transaction-service")

                .setTransactionId(value(transaction.id()))
                .setCustomerId(value(transaction.customerId()))
                .setSourceProductId(value(transaction.sourceProductId()))
                .setTargetProductId(value(transaction.targetProductId()))
                .setSourceProductType(
                        transaction.sourceProductType() != null
                                ? transaction.sourceProductType().name()
                                : null
                )
                .setTargetProductType(
                        transaction.targetProductType() != null
                                ? transaction.targetProductType().name()
                                : null
                )
                .setTransactionType(transaction.type().name())
                .setStatus(transaction.status().name())
                .setAmount(transaction.amount().doubleValue())
                .setCommission(transaction.commission().doubleValue())

                .setDescription(value(transaction.description()))
                .build();
    }


    public TransactionCompletedEvent toTransactionCompletedEvent(String transactionId) {

        return TransactionCompletedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("TRANSACTION_COMPLETED")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("transaction-service")
                .setTransactionId(value(transactionId))
                .build();
    }

    public TransactionFailedEvent toTransactionFailedEvent(
            String transactionId,
            String reason) {

        return TransactionFailedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("TRANSACTION_FAILED")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("transaction-service")
                .setTransactionId(value(transactionId))
                .setReason(value(reason))
                .build();
    }

    public TransactionDeletedEvent toTransactionDeletedEvent(Transaction transaction) {

        return TransactionDeletedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("TRANSACTION_DELETED")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("transaction-service")
                .setTransactionId(value(transaction.id()))
                .build();
    }

    private String value(String value) {
        return value == null ? "" : value;
    }

    public SpecificRecordBase toTransactionUpdatedEvent(Transaction transaction) {

        return TransactionCreatedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("TRANSACTION_UPDATED")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("transaction-service")

                .setTransactionId(value(transaction.id()))
                .setCustomerId(value(transaction.customerId()))
                .setSourceProductId(value(transaction.sourceProductId()))
                .setTargetProductId(value(transaction.targetProductId()))
                .setSourceProductType(
                        transaction.sourceProductType() != null
                                ? transaction.sourceProductType().name()
                                : null
                )
                .setTargetProductType(
                        transaction.targetProductType() != null
                                ? transaction.targetProductType().name()
                                : null
                )
                .setTransactionType(transaction.type().name())
                .setStatus(transaction.status().name())
                .setAmount(transaction.amount().doubleValue())
                .setCommission(transaction.commission().doubleValue())

                .setDescription(value(transaction.description()))
                .build();
    }
}