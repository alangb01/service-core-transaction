package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.in.rest.handler;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.transactionservice.domain.exception.AccountNotFoundException;
import pe.nom.charlygastelo.app.transactionservice.domain.exception.CreditNotFoundException;
import pe.nom.charlygastelo.app.transactionservice.domain.exception.InsufficientBalanceException;
import pe.nom.charlygastelo.app.transactionservice.domain.exception.InvalidTransactionException;
import pe.nom.charlygastelo.app.transactionservice.domain.exception.OverdueDebtException;
import pe.nom.charlygastelo.app.transactionservice.domain.exception.TransactionBusinessException;
import pe.nom.charlygastelo.app.transactionservice.domain.exception.TransactionNotFoundException;
import pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.in.rest.response.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTransactionNotFound(
            TransactionNotFoundException ex,
            ServerWebExchange exchange) {

        log.warn("Transaction not found: {}", ex.getMessage());

        return buildResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );
    }

    @ExceptionHandler({
            AccountNotFoundException.class,
            CreditNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            RuntimeException ex,
            ServerWebExchange exchange) {

        log.warn("Resource not found: {}", ex.getMessage());

        return buildResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );
    }

    @ExceptionHandler({
            InvalidTransactionException.class,
            InsufficientBalanceException.class,
            OverdueDebtException.class,
            TransactionBusinessException.class
    })
    public ResponseEntity<ErrorResponse> handleBusinessException(
            RuntimeException ex,
            ServerWebExchange exchange) {

        log.warn("Business validation failed: {}", ex.getMessage());

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex,
            ServerWebExchange exchange) {

        log.error("Unexpected error", ex);

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                exchange.getRequest().getPath().value()
        );
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            String path) {

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();

        return ResponseEntity.status(status).body(response);
    }
}