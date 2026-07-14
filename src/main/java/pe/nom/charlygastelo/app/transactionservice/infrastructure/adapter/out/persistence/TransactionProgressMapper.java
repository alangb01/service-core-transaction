package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.persistence;

import org.springframework.stereotype.Component;
import pe.nom.charlygastelo.app.transactionservice.domain.model.TransactionProgress;

@Component
public class TransactionProgressMapper {

    public TransactionProgress toDomain(TransactionProgressDocument doc) {
        return new TransactionProgress(
                doc.getTransactionId(),
                doc.getType(),
                doc.isAccountWithdrawOccurred(),
                doc.isAccountDepositOccurred(),
                doc.isCreditPaymentOccurred(),
                doc.isCreditCardChargeOccurred(),
                doc.isMovementRecorded(),
                doc.isFailed(),
                doc.getUpdatedAt()
        );
    }

    public TransactionProgressDocument toDocument(TransactionProgress domain) {
        TransactionProgressDocument doc = new TransactionProgressDocument();

        doc.setTransactionId(domain.transactionId());
        doc.setType(domain.type());

        doc.setAccountWithdrawOccurred(domain.accountWithdrawOccurred());
        doc.setAccountDepositOccurred(domain.accountDepositOccurred());
        doc.setCreditPaymentOccurred(domain.creditPaymentOccurred());
        doc.setCreditCardChargeOccurred(domain.creditCardChargeOccurred());
        doc.setMovementRecorded(domain.movementRecorded());

        doc.setFailed(domain.failed());
        doc.setUpdatedAt(domain.updatedAt());

        return doc;
    }
}
