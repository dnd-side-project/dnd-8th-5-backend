package com.dnd.modutime.core.common;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // (기본 공통 에러)
    MT400("잘못된 요청입니다."),
    MT401("인증에 실패하였습니다."),
    MT403("권한이 없습니다."),
    MT404("리소스를 찾을 수 없습니다."),
    MT500("서버 내부 오류가 발생하였습니다."),

    DEFAULT("HTTP 기본 에러"),
    ACCESS_TOKEN_EXPIRED("access 토큰이 만료됨"),
    REFRESH_TOKEN_EXPIRED("refresh 토큰이 만료됨"),
    MISSING_COOKIE("쿠키가 존재하지 않음"),
    INVALID_TOKEN("유효하지 않은 토큰"),
    BAD_CREDENTIALS("잘못된 자격 증명"),
    INVALID_AUTHORIZATION_HEADER("인증 헤더가 잘못됨"),
    INFRASTRUCTURE_ERROR("외부 호출 오류"),
    INTERNAL_SERVER_ERROR("서버 내부 오류"),
    USER_NOT_FOUND("유저를 찾을 수 없음"),
    ;

    private final String description;

    ErrorCode(String description) {
        this.description = description;
    }

    public String getCode() {
        return this.name();
    }
}
