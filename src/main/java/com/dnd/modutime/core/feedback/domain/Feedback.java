package com.dnd.modutime.core.feedback.domain;

import com.dnd.modutime.core.entity.Auditable;
import com.dnd.modutime.core.feedback.domain.converter.ResponsesJsonConverter;
import com.dnd.modutime.core.feedback.domain.converter.SnapshotJsonConverter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feedback implements Auditable {

    private static final int CONTENT_MAX_LENGTH = 1000;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FeedbackCategory category;

    @Column(nullable = false, length = CONTENT_MAX_LENGTH)
    private String content;

    @Column(name = "reply_email")
    private String replyEmail;

    @Column(name = "interview_agreed", nullable = false)
    private boolean interviewAgreed;

    @Column(name = "interview_phone_number", length = 20)
    private String interviewPhoneNumber;

    @Convert(converter = ResponsesJsonConverter.class)
    @Column(nullable = false, columnDefinition = "TEXT")
    private Responses responses;

    @Convert(converter = SnapshotJsonConverter.class)
    @Column(nullable = false, columnDefinition = "TEXT")
    private Snapshot snapshot;

    @Enumerated(EnumType.STRING)
    @Column(name = "author_type", nullable = false, length = 20)
    private AuthorType authorType;

    @Column(name = "author_user_id")
    private Long authorUserId;

    @Column(name = "author_name")
    private String authorName;

    @Column(name = "author_email")
    private String authorEmail;

    private String createdBy;
    private LocalDateTime createdAt;
    private String modifiedBy;
    private LocalDateTime modifiedAt;

    public Feedback(
            FeedbackCategory category,
            String content,
            String replyEmail,
            boolean interviewAgreed,
            String interviewPhoneNumber,
            Responses responses,
            Snapshot snapshot,
            AuthorType authorType,
            Long authorUserId,
            String authorName,
            String authorEmail
    ) {
        validateCategory(category);
        validateContent(content);
        validateResponses(responses);
        validateSnapshot(snapshot);
        validateAuthorType(authorType);

        this.category = category;
        this.content = content;
        this.replyEmail = replyEmail;
        this.interviewAgreed = interviewAgreed;
        this.interviewPhoneNumber = interviewPhoneNumber;
        this.responses = responses;
        this.snapshot = snapshot;
        this.authorType = authorType;
        this.authorUserId = authorUserId;
        this.authorName = authorName;
        this.authorEmail = authorEmail;
    }

    private static void validateCategory(FeedbackCategory category) {
        if (category == null) {
            throw new IllegalArgumentException("category는 필수값입니다.");
        }
    }

    private static void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content는 필수값입니다.");
        }
        if (content.length() > CONTENT_MAX_LENGTH) {
            throw new IllegalArgumentException("content는 최대 " + CONTENT_MAX_LENGTH + "자까지 가능합니다.");
        }
    }

    private static void validateResponses(Responses responses) {
        if (responses == null) {
            throw new IllegalArgumentException("responses는 필수값입니다.");
        }
    }

    private static void validateSnapshot(Snapshot snapshot) {
        if (snapshot == null) {
            throw new IllegalArgumentException("snapshot은 필수값입니다.");
        }
    }

    private static void validateAuthorType(AuthorType authorType) {
        if (authorType == null) {
            throw new IllegalArgumentException("authorType은 필수값입니다.");
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
