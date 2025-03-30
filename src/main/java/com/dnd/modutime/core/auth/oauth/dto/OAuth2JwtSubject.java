package com.dnd.modutime.core.auth.oauth.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class OAuth2JwtSubject {

    private final String registrationId;
    private final String email;

    /**
     * JWT 토큰의 subject 에서 OAuth2 제공자 ID와 이메일을 파싱하여 저장하는 클래스
     *
     * @param subject kakao:example@kakao.com 형식 (provider:email)
     * @throws IllegalArgumentException 제공자 ID와 이메일로 분리할 수 없는 형식의 문자열이 입력된 경우
     */
    public OAuth2JwtSubject(String subject) {
        List<String> subjectParts = List.of(subject.split(":"));

        if (subjectParts.size() != 2) {
            throw new IllegalArgumentException("잘못된 subject 형식입니다.");
        }

        this.registrationId = subjectParts.get(0);
        this.email = subjectParts.get(1);
    }

}
