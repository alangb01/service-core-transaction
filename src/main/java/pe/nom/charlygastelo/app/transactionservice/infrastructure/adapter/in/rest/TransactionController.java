package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.in.rest;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.transactionservice.application.usecase.CreateTransactionUseCase;
import pe.nom.charlygastelo.app.transactionservice.application.usecase.GetTransactionUseCase;
import pe.nom.charlygastelo.app.transactionservice.application.usecase.ListTransactionsUseCase;
import pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.in.rest.mapper.TransactionRestMapper;
import pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.in.rest.request.CreateTransactionRequest;
import pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.in.rest.response.TransactionResponse;

@RestController
@RequestMapping("/transactions")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class TransactionController {

    private final CreateTransactionUseCase createUseCase;
    private final GetTransactionUseCase getUseCase;
    private final ListTransactionsUseCase listUseCase;
    private final TransactionRestMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Single<TransactionResponse> create(
            @RequestBody CreateTransactionRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token
    ) {
        return createUseCase.
                execute(mapper.toDomain(request))
                .map(mapper::toResponse);
    }

    @GetMapping("/{id}")
    public Single<TransactionResponse> findById(@PathVariable String id) {
        return getUseCase.byId(id)
                .map(mapper::toResponse)
                .toSingle();
    }

    @GetMapping
    public Flowable<TransactionResponse> findAll() {
        return listUseCase.all()
                .map(mapper::toResponse);
    }

}