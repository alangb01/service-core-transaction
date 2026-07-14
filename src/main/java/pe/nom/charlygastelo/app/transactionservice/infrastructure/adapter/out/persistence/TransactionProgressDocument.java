package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.persistence;

import java.time.Instant;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import pe.nom.charlygastelo.app.transactionservice.domain.model.TransactionType;


@Data
@Document("transaction_progress")
public class TransactionProgressDocument {

    private String transactionId;
    private TransactionType type;

    private boolean accountWithdrawOccurred;
    private boolean accountDepositOccurred;
    private boolean creditPaymentOccurred;
    private boolean creditCardChargeOccurred;
    private boolean movementRecorded;

    private boolean failed;

    private Instant updatedAt;
}
