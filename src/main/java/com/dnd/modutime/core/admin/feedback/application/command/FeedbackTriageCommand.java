package com.dnd.modutime.core.admin.feedback.application.command;

import com.dnd.modutime.core.admin.feedback.domain.Severity;
import com.dnd.modutime.core.admin.feedback.domain.TriageStatus;

/**
 * 트리아지 부분 수정 커맨드. 두 값 모두 선택값이며, {@code null} 은 미변경을 의미한다.
 */
public record FeedbackTriageCommand(Severity severity, TriageStatus status) {

    public boolean isEmpty() {
        return severity == null && status == null;
    }
}
