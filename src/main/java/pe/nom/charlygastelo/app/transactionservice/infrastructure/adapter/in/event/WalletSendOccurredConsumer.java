package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.in.event;

import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.shared.avro.dto.WalletPaymentOccurredEvent;
import pe.nom.charlygastelo.app.transactionservice.application.usecase.CreateTransactionUseCase;
import pe.nom.charlygastelo.app.transactionservice.domain.model.ProductType;
import pe.nom.charlygastelo.app.transactionservice.domain.model.Transaction;
import pe.nom.charlygastelo.app.transactionservice.domain.model.TransactionStatus;
import pe.nom.charlygastelo.app.transactionservice.domain.model.TransactionType;


@Component
@RequiredArgsConstructor
@Slf4j
public class WalletSendOccurredConsumer {

    private final CreateTransactionUseCase createTransactionUseCase;

    @KafkaListener(topics = "${topic.wallet-payment-occurred}", groupId = "transaction-service")
    public void consume(WalletPaymentOccurredEvent event) {

        String eventId = event.getEventId().toString();
        String eventType = event.getEventType().toString();

        log.info("[TX] Received {} eventId={}", eventType, eventId);

        Transaction tx = new Transaction(
                null,
                event.getCustomerId().toString(),
                event.getSourceDebitCardId().toString(),
                event.getTargetDebitCardId().toString(),
                ProductType.DEBIT_CARD,
                ProductType.DEBIT_CARD,
                TransactionType.YANKI_SEND,
                TransactionStatus.PENDING,
                new BigDecimal(event.getAmount()),
                BigDecimal.ZERO,
                event.getDescription().toString(),
                Instant.now(),
                null
        );

        createTransactionUseCase.execute(tx).subscribe();
    }
}
