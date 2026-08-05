package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.in.rest;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.reactivex.rxjava3.core.Single;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.transactionservice.application.command.TransactionCommand;
import pe.nom.charlygastelo.app.transactionservice.application.usecase.CreateTransactionUseCase;
import pe.nom.charlygastelo.app.transactionservice.application.usecase.GetTransactionUseCase;
import pe.nom.charlygastelo.app.transactionservice.application.usecase.ListTransactionsUseCase;
import pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.in.rest.mapper.TransactionRestMapper;
import pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.in.rest.request.CreateTransactionRequest;
import pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.in.rest.response.PageResponse;
import pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.in.rest.response.TransactionResponse;

@RestController
@RequestMapping("/api/transactions")
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
        TransactionCommand cmd = new TransactionCommand(
                null,
                request.customerId(),
                request.sourceProductId(),
                request.targetProductId(),
                request.sourceProductType(),
                request.targetProductType(),
                request.type(),
                request.amount(),
                request.commission(),
                request.description()
        );

        return createUseCase.
                execute(cmd)
                .map(mapper::toResponse);
    }

    @GetMapping("/{id}")
    public Single<TransactionResponse> findById(@PathVariable String id) {
        return getUseCase.byId(id)
                .map(mapper::toResponse)
                .toSingle();
    }

    @GetMapping
    public Single<PageResponse<TransactionResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return listUseCase.all(page, size)
                .map(result -> PageResponse.from(
                        result,
                        mapper::toResponse
                ));
    }

}