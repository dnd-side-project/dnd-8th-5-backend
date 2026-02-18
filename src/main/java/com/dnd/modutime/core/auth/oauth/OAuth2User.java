package com.dnd.modutime.core.auth.oauth;

import com.dnd.modutime.core.auth.security.ModutimeUserDetails;
import com.dnd.modutime.core.user.OAuth2Provider;
import com.dnd.modutime.core.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public record OAuth2User(
        User user,
        Map<String, Object> attributes,
        String attributeKey
) implements org.springframework.security.oauth2.core.user.OAuth2User, ModutimeUserDetails {

    @Override
    public String getName() {
        return this.attributes.get(this.attributeKey).toString();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")); // TODO :: 권한 정책 정의 후 설정 필요
    }

    @Override
    public String getPassword() {
        return null;
    }

    /**
     * 사용자의 고유한 식별자(username)를 반환합니다.
     *
     * 이 메서드는 Spring Security의 인증 과정에서 사용자의 고유한 식별자로 사용됩니다.
     * `registrationId`와 `email`을 결합하여 유일한 식별자를 생성합니다.
     *
     * @return 고유한 식별자 (예: "kakao:user@example.com")
     */
    @Override
    public String getUsername() {
        return this.user.getProvider().getRegistrationId() + ":" + this.user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public OAuth2Provider getProvider() {
        return this.user.getProvider();
    }

}
