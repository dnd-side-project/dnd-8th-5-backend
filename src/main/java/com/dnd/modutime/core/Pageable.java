package com.dnd.modutime.core;

/**
 * 페이징 요청 파라미터 인터페이스
 */
public interface Pageable {
    /**
     * 페이지 번호
     *
     * @return 페이지번호
     */
    int getPage();

    /**
     * 페이징 혹은 오프렛에서 요청크기로 지정
     *
     * @return 페이지크기
     */
    int getSize();

    /**
     * 오프셋 시작위치
     *
     * @return 오프셋데이터
     */
    long getOffset();
}

