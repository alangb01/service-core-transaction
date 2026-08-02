package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.persistence;

import java.util.ArrayList;
import java.util.HashSet;
import org.springframework.stereotype.Component;
import pe.nom.charlygastelo.app.transactionservice.domain.model.TransactionProgress;


@Component
public class TransactionProgressMapper {

    public TransactionProgress toDomain(TransactionProgressDocument doc) {
        return new TransactionProgress(
                doc.getTransactionId(),
                doc.getType(),
                new HashSet<>(doc.getReceivedEvents()),
                new HashSet<>(doc.getRequiredEvents()),
                doc.isFailed(),
                doc.getUpdatedAt()
        );
    }

    public TransactionProgressDocument toDocument(TransactionProgress domain) {
        TransactionProgressDocument doc = new TransactionProgressDocument();

        doc.setTransactionId(domain.transactionId());
        doc.setType(domain.type());

        doc.setReceivedEvents(new ArrayList<>(domain.receivedEvents()));
        doc.setRequiredEvents(new ArrayList<>(domain.requiredEvents()));

        doc.setFailed(domain.failed());
        doc.setUpdatedAt(domain.updatedAt());

        return doc;
    }
}
