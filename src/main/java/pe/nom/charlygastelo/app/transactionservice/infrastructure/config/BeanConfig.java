package pe.nom.charlygastelo.app.transactionservice.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.transactionservice.application.usecase.*;
import pe.nom.charlygastelo.app.transactionservice.domain.port.TransactionManagementEventProducerPort;
import pe.nom.charlygastelo.app.transactionservice.domain.port.TransactionRepositoryPort;

@Configuration
@RequiredArgsConstructor
public class BeanConfig {



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
            TransactionManagementEventProducerPort producer) {

        return new DeleteTransactionUseCase(
                repository,
                producer
        );
    }




}