package com.dnd.modutime.core.user;

import com.dnd.modutime.core.entity.Auditable;
import com.dnd.modutime.util.DateTimeUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "token_expiration_time")
    private LocalDateTime tokenExpirationTime;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "last_modified_time")
    private LocalDateTime lastModifiedTime;

    public User(final String name,
                final String email,
                final String profileImage,
                final String thumbnailImage,
                final OAuth2Provider provider
    ) {
        this.name = Objects.requireNonNull(name);
        this.email = Objects.requireNonNull(email);
        this.profileImage = Objects.requireNonNull(profileImage);
        this.thumbnailImage = Objects.requireNonNull(thumbnailImage);
        this.provider = Objects.requireNonNull(provider);
        this.createdTime = DateTimeUtils.currentUTC();
        this.lastModifiedTime = DateTimeUtils.currentUTC();
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

}
