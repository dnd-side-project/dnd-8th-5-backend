package com.dnd.modutime.core.admin.feedback.application.response;

import com.dnd.modutime.core.admin.feedback.domain.FeedbackTriage;
import com.dnd.modutime.core.feedback.domain.Feedback;
import com.dnd.modutime.core.feedback.domain.FeedbackCategory;
import com.dnd.modutime.core.feedback.domain.ResponsePair;
import com.dnd.modutime.core.feedback.domain.Snapshot;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 어드민 피드백 상세 응답. 목록 row 와 동일한 필드에 더해 제출 시점 컨텍스트인 {@code snapshot} 을 포함한다.
 *
 * <p>{@code snapshot} 은 제출 시 클라이언트가 보낸 구조를 그대로 직렬화하므로 내부 키는 camelCase 다.
 * 미수집 시 {@code null}.</p>
 */
public record FeedbackDetailResponse(
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
        @JsonProperty("updated_at") LocalDateTime updatedAt,
        Snapshot snapshot
) {

    public static FeedbackDetailResponse of(Feedback feedback, FeedbackTriage triage) {
        var row = FeedbackRowResponse.of(feedback, triage);
        return new FeedbackDetailResponse(
                row.id(),
                row.category(),
                row.content(),
                row.replyEmail(),
                row.interviewAgreed(),
                row.interviewPhoneNumber(),
                row.responses(),
                row.severity(),
                row.status(),
                row.createdAt(),
                row.updatedAt(),
                feedback.getSnapshot()
        );
    }
}
