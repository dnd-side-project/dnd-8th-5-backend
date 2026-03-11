package com.dnd.modutime.core.notification.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notification",
        indexes = {
                @Index(name = "idx_notification_recipient_read", columnList = "recipient_id, is_read"),
                @Index(name = "idx_notification_recipient_created", columnList = "recipient_id, created_at")
        })
@Getter
public class Notification {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(name = "recipient_id", nullable = false)
    private Long recipientId;

    @Convert(converter = MapToJsonConverter.class)
    @Column(name = "data", columnDefinition = "TEXT")
    private Map<String, String> data;

    @Column(name = "is_read", nullable = false)
    private boolean read;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "sent", nullable = false)
    private boolean sent;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static Notification of(NotificationType type,
                                   String title,
                                   String message,
                                   Long recipientId,
                                   Map<String, String> data) {
        return new Notification(type, title, message, recipientId, data);
    }

    private Notification(NotificationType type,
                         String title,
                         String message,
                         Long recipientId,
                         Map<String, String> data) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.recipientId = recipientId;
        this.data = data;
        this.read = false;
        this.sent = false;
    }

    public void markAsSent(LocalDateTime now) {
        if (!this.sent) {
            this.sent = true;
            this.sentAt = now;
        }
    }

    public void markAsRead(LocalDateTime now) {
        if (!this.read) {
            this.read = true;
            this.readAt = now;
        }
    }
}
