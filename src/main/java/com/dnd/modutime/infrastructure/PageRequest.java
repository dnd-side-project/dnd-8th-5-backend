package com.dnd.modutime.infrastructure;

import com.dnd.modutime.core.Pageable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PageRequest implements Pageable {
    public static final Integer DEFAULT_PAGE = 0;
    public static final Integer DEFAULT_SIZE = 20;

    private Integer page;
    private Integer size;

    public PageRequest(Integer page, Integer size) {
        this.page = page == null ? DEFAULT_PAGE : page;
        this.size = size == null ? DEFAULT_SIZE : size;
    }

    /**
     * 페이지 요청 반환
     *
     * @param page 페이지번호
     * @param size 페이지 크기
     * @return 페이지 요청 개체
     */
    public static PageRequest of(int page, int size) {
        if (page < 0) {
            page = 0;
        }

        if (size < 1) {
            size = 20;
        } else if (size > 1000) {
            size = 1000;
        }

        return new PageRequest(page, size);
    }

    public int getPage() {
        return page > 0 ? page - 1 : 0;
    }

    public int getSize() {
        return size;
    }

    public long getOffset() {
        return (long) getPage() * getSize();
    }
}
