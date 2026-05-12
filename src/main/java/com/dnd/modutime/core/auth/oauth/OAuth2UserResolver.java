package com.dnd.modutime.core.auth.oauth;

import com.dnd.modutime.core.auth.oauth.exception.KakaoEmailNotProvidedException;
import com.dnd.modutime.core.user.User;
import com.dnd.modutime.core.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * OAuth2 사용자 조회/생성/캐시를 공통화한 리졸버.
 * <p>
 * 웹 흐름({@link OAuth2UserService#loadUser})과 네이티브 흐름(KakaoNativeLoginCommandHandler)이
 * 모두 동일한 사용자 조회/생성/캐시 동작을 수행하므로, 중복을 제거하기 위해 분리했다.
 *
 * <p>호출자는 {@code @Transactional} 컨텍스트 안에서 호출해야 한다.
 * (find-or-save 시 LazyInitializationException 방지 및 동일 트랜잭션에서의 캐시 일관성 유지를 위해)
 */
@Slf4j
@Service
public class OAuth2UserResolver {

    private final UserRepository userRepository;
    private final UserCache userCache;

    public OAuth2UserResolver(final UserRepository userRepository, final UserCache userCache) {
        this.userRepository = userRepository;
        this.userCache = userCache;
    }

    /**
     * 캐시 → DB 순으로 사용자를 조회하고, 없으면 새로 생성한 뒤 캐시에 적재한다.
     *
     * @param details                {@link OAuth2UserDetails#of(String, Map, com.fasterxml.jackson.databind.ObjectMapper)}로 파싱된 OAuth2 사용자 정보
     * @param attributes             OAuth2 provider 원본 attribute 맵 (캐시되는 {@link OAuth2User}에 그대로 보관)
     * @param userNameAttributeName  {@link OAuth2User#getName()}에 사용할 attribute 키
     * @return 캐시 또는 신규 적재된 {@link OAuth2User}
     */
    public OAuth2User resolveAndCache(final OAuth2UserDetails details,
                                       final Map<String, Object> attributes,
                                       final String userNameAttributeName) {
        if (details.email() == null) {
            throw new KakaoEmailNotProvidedException("카카오 계정 이메일 제공에 동의해야 로그인할 수 있습니다.");
        }
        String cacheKey = details.oAuth2Provider().getRegistrationId() + ":" + details.email();
        UserDetails cached = this.userCache.getUserFromCache(cacheKey);
        if (cached != null) {
            return (OAuth2User) cached;
        }

        User user = getOrSaveUser(details);
        OAuth2User oAuth2User = new OAuth2User(user, attributes, userNameAttributeName);
        this.userCache.putUserInCache(oAuth2User);
        log.debug("User {} is cached", oAuth2User.getUsername());
        return oAuth2User;
    }

    private User getOrSaveUser(final OAuth2UserDetails details) {
        User user = this.userRepository.findByEmailAndProvider(details.email(), details.oAuth2Provider())
                .orElseGet(details::toEntity);
        user.linkOAuthIdIfAbsent(details.oauthId());
        return this.userRepository.save(user);
    }
}
