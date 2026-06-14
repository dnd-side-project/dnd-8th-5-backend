package com.dnd.modutime.core.feedback.application.command;

import com.dnd.modutime.core.feedback.domain.AuthorType;

/**
 * 서버가 Authorization 헤더를 검증해 판정한 제출자 식별 정보. 클라이언트가 보낸 snapshot.user와 달리 신뢰 가능한 값이다.
 */
public record FeedbackAuthor(AuthorType type, Long userId, String name, String email) {

    public static FeedbackAuthor anonymous() {
        return new FeedbackAuthor(AuthorType.ANONYMOUS, null, null, null);
    }

    public static FeedbackAuthor guest(String name) {
        return new FeedbackAuthor(AuthorType.GUEST, null, name, null);
    }

    public static FeedbackAuthor member(Long userId, String name, String email) {
        return new FeedbackAuthor(AuthorType.MEMBER, userId, name, email);
    }
}
