package pe.nom.charlygastelo.app.transactionservice.infrastructure.adapter.in.rest.response;


import java.util.List;
import java.util.function.Function;
import pe.nom.charlygastelo.app.transactionservice.domain.model.Page;

public record PageResponse<T>(
        int page,
        int size,
        long total,
        long totalPages,
        List<T> data
) {
    public static <A, B> PageResponse<B> from(Page<A> page, Function<A, B> mapper) {
        return new PageResponse<>(
                page.getPage(),
                page.getSize(),
                page.getTotal(),
                page.getTotalPages(),
                page.getContent().stream().map(mapper).toList()
        );
    }
}
