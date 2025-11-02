package com.dnd.modutime.core;

import java.util.List;

public interface Page<T> {

    /**
     * 이전 페이지 존재여부 반환
     *
     * @return 이전 페이지 존재여부
     */
    boolean hasPrevious();

    /**
     * 다음 페이지 존재여부 반환
     *
     * @return 다음페이지 존재여부
     */
    boolean hasNext();

    /**
     * 페이징 검색조건 포함여부 반환
     *
     * @return 페이징 검색조건 포함여부
     */
    boolean hasContent();

    /**
     * 첫번째 페이지 여부 반환
     *
     * @return 첫번째 페이지 여부
     */
    boolean isFirst();

    /**
     * 마지막 페이지 여부 반환
     *
     * @return 마지막 페이지 여부
     */
    boolean isLast();

    /**
     * 현재 페이지에 포함된 검색결과 반환
     *
     * @return 현재 페이지에 포함된 검색결과
     */
    List<T> getContent();

    /**
     * 페이징 조건 반환
     *
     * @return 페이징 조건
     */
    Pageable getPageRequest();

    /**
     * 해당검색 페이징 조건 전체 건수 반환
     *
     * @return 해당검색 페이징 조건 전체 건수
     */
    long getTotal();

    /**
     * 전체 페이지 수 반환
     *
     * @return 전체 페이지 수 반환
     */
    int getTotalPages();
}
