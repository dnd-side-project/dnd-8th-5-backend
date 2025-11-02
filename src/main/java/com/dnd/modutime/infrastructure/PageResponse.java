package com.dnd.modutime.infrastructure;

import com.dnd.modutime.core.Page;
import com.dnd.modutime.core.Pageable;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PageResponse<T> implements Page<T> {
    public static final int FIRST_PAGE = 1;

    private final List<T> content = new ArrayList<>();
    private PageRequest pageRequest;
    private long total;

    public PageResponse(List<T> content, Pageable pageable, long total) {
        this.content.addAll(content);
        this.pageRequest = PageRequest.of(pageable.getPage(), pageable.getSize());
        this.total =
                Optional.of(pageable)
                        .filter(it -> !content.isEmpty())
                        .filter(it -> pageable.getOffset() + it.getSize() > total)
                        .map(it -> pageable.getOffset() + content.size())
                        .orElse(total);
    }

    public static <T> PageResponse<T> of(List<T> content, Pageable pageable, long total) {
        return new PageResponse<>(content, pageable, total);
    }

    @Override
    public int getTotalPages() {
        if (this.pageRequest.getSize() == 0) {
            return FIRST_PAGE;
        }

        return (int) Math.ceil(getTotal() / (double) this.pageRequest.getSize());
    }

    @JsonProperty
    @Override
    public boolean hasPrevious() {
        return this.pageRequest.getPage() > 0;
    }

    @JsonProperty
    @Override
    public boolean hasNext() {
        return this.pageRequest.getPage() + FIRST_PAGE < getTotalPages();
    }

    @JsonProperty
    @Override
    public boolean hasContent() {
        return !getContent().isEmpty();
    }

    @JsonProperty("isFirst")
    @Override
    public boolean isFirst() {
        return !hasPrevious();
    }

    @JsonProperty("isLast")
    @Override
    public boolean isLast() {
        return !hasNext();
    }

    @Override
    public List<T> getContent() {
        return content;
    }

    @Override
    public Pageable getPageRequest() {
        return pageRequest;
    }

    @Override
    public long getTotal() {
        return total;
    }
}
