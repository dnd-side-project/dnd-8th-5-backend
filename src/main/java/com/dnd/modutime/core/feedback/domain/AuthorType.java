package com.dnd.modutime.core.feedback.domain;

/**
 * 피드백 제출자 유형. 서버가 Authorization 헤더를 검증해 판정하는 신뢰 값이다.
 *
 * <ul>
 *     <li>{@code MEMBER} - 카카오 OAuth 로그인 회원</li>
 *     <li>{@code GUEST} - 비회원 로그인(guest access token) 참여자</li>
 *     <li>{@code ANONYMOUS} - 토큰 없음 또는 만료/무효 토큰</li>
 * </ul>
 */
public enum AuthorType {
    MEMBER,
    GUEST,
    ANONYMOUS,
}
