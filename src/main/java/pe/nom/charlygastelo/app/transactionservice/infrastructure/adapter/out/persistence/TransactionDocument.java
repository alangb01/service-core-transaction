package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.out.persistence;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.nom.charlygastelo.app.transactionservice.domain.model.ProductType;
import pe.nom.charlygastelo.app.transactionservice.domain.model.TransactionStatus;
import pe.nom.charlygastelo.app.transactionservice.domain.model.TransactionType;

@Document(collection = "transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDocument {

    @Id
    private String id;

    private String customerId;
    private String sourceProductId;
    private String targetProductId;

    private ProductType sourceProductType;
    private ProductType targetProductType;

    private TransactionType type;
    private TransactionStatus status;

    private BigDecimal amount;
    private BigDecimal commission;
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}