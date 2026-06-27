package pe.nom.charlygastelo.app.transactionservice.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.transactionservice.application.usecase.CreateTransactionUseCase;
import pe.nom.charlygastelo.app.transactionservice.application.usecase.DeleteTransactionUseCase;
import pe.nom.charlygastelo.app.transactionservice.application.usecase.GetTransactionUseCase;
import pe.nom.charlygastelo.app.transactionservice.application.usecase.ListTransactionsUseCase;
import pe.nom.charlygastelo.app.transactionservice.domain.port.TransactionEventProducerPort;
import pe.nom.charlygastelo.app.transactionservice.domain.port.TransactionRepositoryPort;

@Configuration
@RequiredArgsConstructor
public class BeanConfig {

    @Bean
    public CreateTransactionUseCase createTransactionUseCase(
            TransactionRepositoryPort repository,
            TransactionEventProducerPort producer) {

        return new CreateTransactionUseCase(
                repository,
                producer
        );
    }

    @Bean
    public GetTransactionUseCase getTransactionUseCase(
            TransactionRepositoryPort repository) {

        return new GetTransactionUseCase(repository);
    }

    @Bean
    public ListTransactionsUseCase listTransactionsUseCase(
            TransactionRepositoryPort repository) {

        return new ListTransactionsUseCase(repository);
    }

    @Bean
    public DeleteTransactionUseCase deleteTransactionUseCase(
            TransactionRepositoryPort repository,
            TransactionEventProducerPort producer) {

        return new DeleteTransactionUseCase(
                repository,
                producer
        );
    }

}