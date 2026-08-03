package pe.nom.charlygastelo.app.transactionservice.application.usecase.yanki;

import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.transactionservice.application.command.TransactionCommand;
import pe.nom.charlygastelo.app.transactionservice.application.command.TransactionYankiCommand;
import pe.nom.charlygastelo.app.transactionservice.application.usecase.CreateTransactionUseCase;
import pe.nom.charlygastelo.app.transactionservice.domain.model.DebitCard;
import pe.nom.charlygastelo.app.transactionservice.domain.model.ProductType;
import pe.nom.charlygastelo.app.transactionservice.domain.model.Transaction;
import pe.nom.charlygastelo.app.transactionservice.domain.model.TransactionType;
import pe.nom.charlygastelo.app.transactionservice.domain.port.DebitCardRepositoryPort;


@Component
@Slf4j
@RequiredArgsConstructor
public class CreateTransactionYankiSendUseCase {
    private final DebitCardRepositoryPort debitCardRepository;
    private final CreateTransactionUseCase createTransactionUseCase;

    public Single<Transaction> execute(TransactionYankiCommand yankiCmd) {

        log.info("[TX] Processing accounts resolved for yanki");

        return debitCardRepository.requestById(yankiCmd.sourceProductId())
                .flatMap(sourceDebitCard ->
                    debitCardRepository.requestById(yankiCmd.targetProductId())
                        .flatMap(targetDebitCard -> prepararTransaction(
                                yankiCmd,
                                sourceDebitCard,
                                targetDebitCard))
                        .flatMap(createTransactionUseCase::execute)
                );
    }

    private Single<TransactionCommand> prepararTransaction(
            TransactionYankiCommand yankiCmd,
            DebitCard sourceDebitCard,
            DebitCard targetDebitCard
    ) {
        return Single.just(new TransactionCommand(
                null,
                yankiCmd.customerId(),
                sourceDebitCard.accountId(),
                targetDebitCard.accountId(),
                ProductType.ACCOUNT.name(),
                ProductType.ACCOUNT.name(),
                TransactionType.YANKI_SEND.name(),
                yankiCmd.amount(),
                yankiCmd.commission(),
                yankiCmd.description()
        ));
    }
}
