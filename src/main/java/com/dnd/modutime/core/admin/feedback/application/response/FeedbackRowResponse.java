package com.dnd.modutime.core.admin.feedback.application.response;

import com.dnd.modutime.core.admin.feedback.domain.FeedbackTriage;
import com.dnd.modutime.core.admin.feedback.domain.Severity;
import com.dnd.modutime.core.admin.feedback.domain.TriageStatus;
import com.dnd.modutime.core.feedback.domain.Feedback;
import com.dnd.modutime.core.feedback.domain.FeedbackCategory;
import com.dnd.modutime.core.feedback.domain.ResponsePair;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 어드민 피드백 목록/트리아지 응답 한 건(row). 최상위 필드는 프론트 계약에 맞춰 snake_case 로 노출한다.
 * 목록에서는 {@code snapshot} 을 포함하지 않는다.
 *
 * <p>{@code severity}/{@code status} 는 트리아지가 없으면 기본값({@code low}/{@code open})으로 채운다.
 * {@code created_at} 은 피드백 제출 시각, {@code updated_at} 은 트리아지 최종 변경 시각(없으면 피드백 수정 시각)이다.</p>
 */
public record FeedbackRowResponse(
        Long id,
        FeedbackCategory category,
        String content,
        @JsonProperty("reply_email") String replyEmail,
        @JsonProperty("interview_agreed") boolean interviewAgreed,
        @JsonProperty("interview_phone_number") String interviewPhoneNumber,
        List<ResponsePair> responses,
        String severity,
        String status,
        @JsonProperty("created_at") LocalDateTime createdAt,
        @JsonProperty("updated_at") LocalDateTime updatedAt
) {

    public static FeedbackRowResponse of(Feedback feedback, FeedbackTriage triage) {
        var severity = (triage != null) ? triage.getSeverity() : Severity.LOW;
        var status = (triage != null) ? triage.getStatus() : TriageStatus.OPEN;
        var updatedAt = (triage != null) ? triage.getModifiedAt() : feedback.getModifiedAt();
        return new FeedbackRowResponse(
                feedback.getId(),
                feedback.getCategory(),
                feedback.getContent(),
                feedback.getReplyEmail(),
                feedback.isInterviewAgreed(),
                feedback.getInterviewPhoneNumber(),
                responsesOf(feedback),
                severity.getValue(),
                status.getValue(),
                feedback.getCreatedAt(),
                updatedAt
        );
    }

    private static List<ResponsePair> responsesOf(Feedback feedback) {
        if (feedback.getResponses() == null) {
            return List.of();
        }
        return feedback.getResponses().values();
    }
}
