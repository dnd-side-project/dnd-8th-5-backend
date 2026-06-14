package com.dnd.modutime.core.admin.feedback.domain;

import com.dnd.modutime.core.entity.Auditable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * 어드민이 피드백을 처리(트리아지)하면서 부여하는 상태(심각도/처리상태)를 보관하는 엔티티.
 *
 * <p>제출 데이터인 {@link com.dnd.modutime.core.feedback.domain.Feedback} 은 불변으로 두고,
 * 어드민 전용 상태만 분리해 별도 테이블({@code feedback_triage})로 관리한다.
 * {@code feedbackId} 당 최대 1개(유니크)이며, 어드민이 처음 트리아지할 때 생성된다(lazy).
 * 트리아지 전에는 목록/상세에서 기본값({@code OPEN}/{@code LOW})으로 노출된다.</p>
 */
@Entity
@Getter
@Table(
        name = "feedback_triage",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_feedback_triage_feedback_id",
                columnNames = "feedback_id"
        )
)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedbackTriage implements Auditable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "feedback_id", nullable = false)
    private Long feedbackId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Severity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TriageStatus status;

    private String createdBy;
    private LocalDateTime createdAt;
    private String modifiedBy;
    private LocalDateTime modifiedAt;

    private FeedbackTriage(Long feedbackId, Severity severity, TriageStatus status) {
        this.feedbackId = feedbackId;
        this.severity = severity;
        this.status = status;
    }

    /**
     * 아직 트리아지된 적 없는 피드백에 대한 기본 상태(미처리/낮음)로 생성한다.
     */
    public static FeedbackTriage initial(Long feedbackId) {
        return new FeedbackTriage(feedbackId, Severity.LOW, TriageStatus.OPEN);
    }

    /**
     * 부분 수정. {@code null} 인 값은 기존 값을 유지한다.
     */
    public void applyTriage(Severity severity, TriageStatus status) {
        if (severity != null) {
            this.severity = severity;
        }
        if (status != null) {
            this.status = status;
        }
    }

    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Override
    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}
