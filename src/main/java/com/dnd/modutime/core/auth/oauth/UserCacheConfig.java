package com.dnd.modutime.core.auth.oauth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserCache;

/**
 * OAuth2 사용자 캐시. OAuth2SecurityConfig가 @Profile("!test") 으로 묶여있어서,
 * 테스트 컨텍스트에서도 UserCache Bean이 필요한 곳(예: UserWithdrawCommandHandler)을 위해
 * 프로파일 무관하게 분리했다.
 */
@Configuration
public class UserCacheConfig {

    @Bean
    public UserCache userCache() {
        return new OAuth2UserCache();
    }
}
