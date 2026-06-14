package com.dnd.modutime.core.admin.feedback.controller.dto;

import com.dnd.modutime.core.admin.feedback.application.command.FeedbackTriageCommand;
import com.dnd.modutime.core.admin.feedback.domain.Severity;
import com.dnd.modutime.core.admin.feedback.domain.TriageStatus;

/**
 * 트리아지(PATCH) 요청 본문. {@code severity}/{@code status} 중 하나 또는 둘 다 보낼 수 있다(부분 수정).
 *
 * <p>값은 소문자 문자열({@code "high"}, {@code "in_progress"} …)로 받는다. 잘못된 값이면
 * {@link Severity#from}/{@link TriageStatus#from} 가 {@link IllegalArgumentException} 을 던져 400 으로 변환된다.</p>
 */
public record FeedbackTriageRequest(String severity, String status) {

    public FeedbackTriageCommand toCommand() {
        return new FeedbackTriageCommand(Severity.from(severity), TriageStatus.from(status));
    }
}
