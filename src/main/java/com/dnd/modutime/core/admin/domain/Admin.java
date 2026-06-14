package com.dnd.modutime.core.admin.domain;

import com.dnd.modutime.core.entity.Auditable;
import com.dnd.modutime.util.DateTimeUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 어드민 계정 엔티티.
 *
 * <p>일반 사용자(카카오 OAuth)와 완전히 분리된 인증 주체이며, username + 비밀번호로 로그인한다.
 * 계정은 운영에서 직접(수동 SQL) 등록하므로 가입 엔드포인트는 제공하지 않는다.
 * 리프레시 토큰은 {@link com.dnd.modutime.core.user.User} 와 동일하게 엔티티 컬럼에 저장한다.</p>
 */
@Entity
@Getter
@Table(name = "admins", uniqueConstraints = {
        @UniqueConstraint(name = "uniqueAdminUsername", columnNames = {"username"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Admin implements Auditable {

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

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password; // TODO:: 평문 저장 -> 해시(BCrypt) 전환 검토

    @Column(name = "refresh_token", length = 512)
    private String refreshToken;

    @Column(name = "token_expiration_time")
    private LocalDateTime tokenExpirationTime;

    public Admin(final String username, final String password) {
        this.username = Objects.requireNonNull(username);
        this.password = Objects.requireNonNull(password);
        this.createdBy = username;
        this.modifiedBy = username;
    }

    /**
     * 입력된 평문 비밀번호가 저장된 비밀번호와 일치하는지 확인한다.
     */
    public boolean matchesPassword(final String rawPassword) {
        return this.password.equals(rawPassword);
    }

    public void updateRefreshToken(final String refreshToken, final LocalDateTime tokenExpireTime) {
        this.refreshToken = Objects.requireNonNull(refreshToken);
        this.tokenExpirationTime = Objects.requireNonNull(tokenExpireTime);
    }

    public boolean isRefreshTokenExpired() {
        return this.tokenExpirationTime == null || this.tokenExpirationTime.isBefore(DateTimeUtils.currentUTC());
    }
}
