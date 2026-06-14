package com.dnd.modutime.core.feedback.application.command;

import com.dnd.modutime.core.feedback.domain.FeedbackCategory;
import com.dnd.modutime.core.feedback.domain.Responses;
import com.dnd.modutime.core.feedback.domain.Snapshot;

/**
 * 피드백 생성에 필요한 도메인 준비형 입력. content는 trim된 상태로 전달된다.
 */
public record FeedbackCreateCommand(
        FeedbackCategory category,
        String content,
        String replyEmail,
        boolean interviewAgreed,
        String interviewPhoneNumber,
        Responses responses,
        Snapshot snapshot,
        FeedbackAuthor author
) {
}
