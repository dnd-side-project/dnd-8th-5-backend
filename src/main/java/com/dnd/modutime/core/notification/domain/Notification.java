package com.dnd.modutime.core.notification.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    @Column(name = "room_uuid", length = 50)
    private String roomUuid;

    @Column(name = "recipient_id", nullable = false)
    private Long recipientId;

    @Column(name = "sender_name", length = 50)
    private String senderName;

    @Column(name = "is_read", nullable = false)
    private boolean read;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Notification(NotificationType type,
                        String title,
                        String message,
                        String roomUuid,
                        Long recipientId,
                        String senderName) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.roomUuid = roomUuid;
        this.recipientId = recipientId;
        this.senderName = senderName;
        this.read = false;
    }

    public void markAsRead(LocalDateTime now) {
        if (!this.read) {
            this.read = true;
            this.readAt = now;
        }
    }
}
