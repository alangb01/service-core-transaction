package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.in.event;

import java.math.BigDecimal;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.shared.avro.dto.WalletPaymentOccurredEvent;
import pe.nom.charlygastelo.app.transactionservice.application.command.TransactionYankiCommand;
import pe.nom.charlygastelo.app.transactionservice.application.usecase.CreateTransactionYankiSendUseCase;


@Component
@RequiredArgsConstructor
@Slf4j
public class WalletSendOccurredConsumer {

    private final CreateTransactionYankiSendUseCase createTransactionYankiSendUseCase;

    @KafkaListener(topics = "${topic.wallet-payment-occurred}", groupId = "transaction-service")
    public void consume(WalletPaymentOccurredEvent event) {

        String eventId = event.getEventId().toString();
        String eventType = event.getEventType().toString();

        log.info("[TX] Received {} eventId={}", eventType, eventId);

        TransactionYankiCommand cmd = new TransactionYankiCommand(
                null,
                event.getCustomerId().toString(),
                event.getSourceDebitCardId().toString(),
                event.getTargetDebitCardId().toString(),
                new BigDecimal(event.getAmount()),
                BigDecimal.ZERO,
                event.getDescription().toString()
        );

        createTransactionYankiSendUseCase.execute(cmd).subscribe();
    }
}
