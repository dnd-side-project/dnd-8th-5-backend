package com.dnd.modutime.core.notification.domain;

import com.dnd.modutime.core.entity.Auditable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "device_token",
        uniqueConstraints = @UniqueConstraint(columnNames = "token"),
        indexes = @Index(name = "idx_device_token_user_id", columnList = "user_id"))
public class DeviceToken implements Auditable {

    @Getter
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, length = 512)
    private String token;

    @Getter
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "device_info", length = 50)
    private String deviceInfo;

    private String createdBy;
    private LocalDateTime createdAt;
    private String modifiedBy;
    private LocalDateTime modifiedAt;

    public DeviceToken(String token, Long userId) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("토큰은 빈 값일 수 없습니다.");
        }
        if (userId == null) {
            throw new IllegalArgumentException("userId는 null일 수 없습니다.");
        }
        this.token = token;
        this.userId = userId;
    }

    public String getToken() {
        return token;
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
