package pe.nom.charlygastelo.app.transactionservice.domain.model;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public record TransactionProgress(
        String transactionId,
        TransactionType type,
        Set<String> receivedEvents,
        Set<String> requiredEvents,
        boolean failed,
        Instant updatedAt
) {

    public TransactionProgress mark(String eventType) {
        Set<String> updated = new HashSet<>(receivedEvents);
        updated.add(eventType);

        return new TransactionProgress(
                transactionId,
                type,
                updated,
                requiredEvents,
                failed,
                Instant.now()
        );
    }

    public boolean hasEvent(String eventType) {
        return receivedEvents.contains(eventType);
    }

    public boolean isCompleted() {
        return receivedEvents.containsAll(requiredEvents) && !failed;
    }

    public Set<String> missingEvents() {
        Set<String> missing = new HashSet<>(requiredEvents);
        missing.removeAll(receivedEvents);
        return missing;
    }

    public TransactionProgress fail() {
        return new TransactionProgress(
                transactionId,
                type,
                receivedEvents,
                requiredEvents,
                true,
                Instant.now()
        );
    }
}
