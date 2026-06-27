package pe.nom.charlygastelo.app.transactionservice.infrastructure.events.mapper;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.shared.avro.dto.TransactionCompletedEvent;
import pe.nom.charlygastelo.app.shared.avro.dto.TransactionCreatedEvent;
import pe.nom.charlygastelo.app.shared.avro.dto.TransactionDeletedEvent;
import pe.nom.charlygastelo.app.shared.avro.dto.TransactionFailedEvent;
import pe.nom.charlygastelo.app.transactionservice.domain.model.Transaction;

@Component
@RequiredArgsConstructor
public class TransactionEventMapper {

    private final ObjectMapper objectMapper;

    // --------------------------------------------------
    // DOMAIN -> AVRO
    // --------------------------------------------------

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
                .setSourceProductType(transaction.sourceProductType().name())
                .setTargetProductType(transaction.targetProductType().name())
                .setTransactionType(transaction.type().name())
                .setStatus(transaction.status().name())
                .setAmount(transaction.amount().doubleValue())
                .setCommission(transaction.commission().doubleValue())
                .setDescription(value(transaction.description()))
                .build();
    }

    public TransactionCompletedEvent toTransactionCompletedEvent(Transaction transaction) {

        return TransactionCompletedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("TRANSACTION_COMPLETED")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("transaction-service")
                .setTransactionId(value(transaction.id()))
                .setCustomerId(value(transaction.customerId()))
                .setSourceProductId(value(transaction.sourceProductId()))
                .setTargetProductId(value(transaction.targetProductId()))
                .setSourceProductType(transaction.sourceProductType().name())
                .setTargetProductType(transaction.targetProductType().name())
                .setTransactionType(transaction.type().name())
                .setAmount(transaction.amount().doubleValue())
                .setCommission(transaction.commission().doubleValue())
                .setDescription(value(transaction.description()))
                .build();
    }

    public TransactionFailedEvent toTransactionFailedEvent(Transaction transaction) {
        return toTransactionFailedEvent(transaction, "Transaction failed");
    }

    public TransactionFailedEvent toTransactionFailedEvent(
            Transaction transaction,
            String reason) {

        return TransactionFailedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("TRANSACTION_FAILED")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("transaction-service")
                .setTransactionId(value(transaction.id()))
                .setCustomerId(value(transaction.customerId()))
                .setTransactionType(transaction.type().name())
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

    // --------------------------------------------------
    // JSON -> AVRO
    // --------------------------------------------------

    public TransactionCreatedEvent toTransactionCreatedEvent(String json) {
        try {
            return objectMapper.readValue(json, TransactionCreatedEvent.class);
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Invalid TransactionCreatedEvent payload", e);
        }
    }

    public TransactionCompletedEvent toTransactionCompletedEvent(String json) {
        try {
            return objectMapper.readValue(json, TransactionCompletedEvent.class);
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Invalid TransactionCompletedEvent payload", e);
        }
    }

    public TransactionFailedEvent toTransactionFailedEvent(String json) {
        try {
            return objectMapper.readValue(json, TransactionFailedEvent.class);
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Invalid TransactionFailedEvent payload", e);
        }
    }

    public TransactionDeletedEvent toTransactionDeletedEvent(String json) {
        try {
            return objectMapper.readValue(json, TransactionDeletedEvent.class);
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Invalid TransactionDeletedEvent payload", e);
        }
    }

    // --------------------------------------------------

    private String value(String value) {
        return value == null ? "" : value;
    }
}