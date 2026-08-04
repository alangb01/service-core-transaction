package pe.nom.charlygastelo.app.transactionservice.domain.model;

import java.util.List;

public class Page<T> {

    private final List<T> content;
    private final int page;
    private final int size;
    private final long total;

    public Page(List<T> content, int page, int size, long total) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.total = total;
    }

    public List<T> getContent() {
        return content;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotal() {
        return total;
    }

    public long getTotalPages() {
        return (long) Math.ceil((double) total / size);
    }
}
