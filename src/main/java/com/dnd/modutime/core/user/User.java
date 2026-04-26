package com.dnd.modutime.core.user;

import com.dnd.modutime.core.entity.Auditable;
import com.dnd.modutime.util.DateTimeUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uniqueEmailAndProvider", columnNames = {"email", "oauth_provider"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Where(clause = "deleted_at IS NULL")
public class User extends AbstractAggregateRoot<User> implements Auditable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter(AccessLevel.PRIVATE)
    private String createdBy;

    @Getter(AccessLevel.PRIVATE)
    private LocalDateTime createdAt;

    @Getter(AccessLevel.PRIVATE)
    private String modifiedBy;

    @Getter(AccessLevel.PRIVATE)
    private LocalDateTime modifiedAt;

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

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email; // TODO:: email 암호화

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "thumbnail_image")
    private String thumbnailImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth_provider")
    private OAuth2Provider provider;

    @Column(name = "oauth_id", length = 64)
    private String oauthId;

    @Column(name = "refresh_token", length = 512)
    private String refreshToken;

    @Column(name = "token_expiration_time")
    private LocalDateTime tokenExpirationTime;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "withdraw_reason", length = 500)
    private String withdrawReason;

    @Column(name = "withdraw_consented_at")
    private LocalDateTime withdrawConsentedAt;

    public User(final String name,
                final String email,
                final String profileImage,
                final String thumbnailImage,
                final OAuth2Provider provider
    ) {
        this(name, email, profileImage, thumbnailImage, provider, null);
    }

    public User(final String name,
                final String email,
                final String profileImage,
                final String thumbnailImage,
                final OAuth2Provider provider,
                final String oauthId
    ) {
        this.name = Objects.requireNonNull(name);
        this.email = Objects.requireNonNull(email);
        this.profileImage = Objects.requireNonNull(profileImage);
        this.thumbnailImage = Objects.requireNonNull(thumbnailImage);
        this.provider = Objects.requireNonNull(provider);
        this.oauthId = oauthId;
        audit();
        registerEvent(new UserCreatedEvent(this.id, DateTimeUtils.currentUTC()));
    }

    private void audit() {
        this.createdBy = this.email;
        this.modifiedBy = this.email;
    }

    public void updateRefreshToken(final String refreshToken, final LocalDateTime tokenExpireTime) {
        this.refreshToken = Objects.requireNonNull(refreshToken);
        this.tokenExpirationTime = Objects.requireNonNull(tokenExpireTime);
    }

    public void expireRefreshToken() {
        this.tokenExpirationTime = DateTimeUtils.currentUTC();
    }

    public boolean isRefreshTokenExpired() {
        return tokenExpirationTime.isBefore(DateTimeUtils.currentUTC());
    }

    /**
     * 카카오 등 OAuth2 provider의 사용자 식별자(provider user id)를 백필한다.
     * 이미 값이 있으면 변경하지 않는다.
     */
    public void linkOAuthIdIfAbsent(final String oauthId) {
        if (this.oauthId == null && oauthId != null) {
            this.oauthId = oauthId;
        }
    }

    /**
     * 회원 탈퇴 처리 (soft delete + 익명화 + 사유/동의 기록).
     * UNIQUE(email, oauth_provider) 제약 회피를 위해 email/oauthId를 변경하고,
     * @Where 절로 조회에서 자동 필터링되도록 deletedAt을 채운다.
     * 탈퇴 사유와 데이터 영구 삭제 동의 시각도 함께 기록한다.
     */
    public void withdraw(final LocalDateTime now, final String reason) {
        Objects.requireNonNull(now);
        Objects.requireNonNull(reason);
        this.deletedAt = now;
        this.email = "withdrawn_" + this.id + "@modutime.local";
        this.oauthId = null;
        this.refreshToken = null;
        this.tokenExpirationTime = now;
        this.withdrawReason = reason;
        this.withdrawConsentedAt = now;
    }

    public boolean isWithdrawn() {
        return this.deletedAt != null;
    }
}
