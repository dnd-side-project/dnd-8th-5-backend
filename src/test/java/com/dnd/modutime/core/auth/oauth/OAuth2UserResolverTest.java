package com.dnd.modutime.core.auth.oauth;

import com.dnd.modutime.core.user.OAuth2Provider;
import com.dnd.modutime.core.user.User;
import com.dnd.modutime.core.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserCache;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@DisplayName("OAuth2UserResolver")
class OAuth2UserResolverTest {

    private static final String EMAIL = "test@example.com";
    private static final String OAUTH_ID = "12345";
    private static final String CACHE_KEY = "kakao:" + EMAIL;
    private static final String USER_NAME_ATTRIBUTE = "id";
    private static final Map<String, Object> ATTRIBUTES = Map.of("id", 12345L);

    private UserRepository userRepository;
    private UserCache userCache;
    private OAuth2UserResolver resolver;

    @BeforeEach
    void setUp() {
        this.userRepository = mock(UserRepository.class);
        this.userCache = mock(UserCache.class);
        this.resolver = new OAuth2UserResolver(userRepository, userCache);
    }

    @Test
    @DisplayName("캐시 hit 시 DB 조회/저장 없이 캐시 사용자 반환")
    void 캐시_히트() {
        var cached = new OAuth2User(newUser(OAUTH_ID), ATTRIBUTES, USER_NAME_ATTRIBUTE);
        when(userCache.getUserFromCache(CACHE_KEY)).thenReturn(cached);

        var result = resolver.resolveAndCache(details(OAUTH_ID), ATTRIBUTES, USER_NAME_ATTRIBUTE);

        assertThat(result).isSameAs(cached);
        verify(userRepository, never()).findByEmailAndProvider(any(), any());
        verify(userRepository, never()).save(any());
        verify(userCache, never()).putUserInCache(any());
    }

    @Test
    @DisplayName("캐시 miss + 사용자 없으면 신규 저장 후 캐시 적재")
    void 신규_가입() {
        when(userCache.getUserFromCache(CACHE_KEY)).thenReturn(null);
        when(userRepository.findByEmailAndProvider(EMAIL, OAuth2Provider.KAKAO)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = resolver.resolveAndCache(details(OAUTH_ID), ATTRIBUTES, USER_NAME_ATTRIBUTE);

        assertThat(result).isNotNull();
        assertThat(result.user().getEmail()).isEqualTo(EMAIL);
        assertThat(result.user().getProvider()).isEqualTo(OAuth2Provider.KAKAO);
        assertThat(result.user().getOauthId()).isEqualTo(OAUTH_ID);
        verify(userRepository, times(1)).save(any(User.class));
        verify(userCache, times(1)).putUserInCache(result);
    }

    @Test
    @DisplayName("캐시 miss + 기존 사용자 있으면 재사용 + oauthId 백필 후 저장")
    void 기존_사용자_oauthId_백필() {
        when(userCache.getUserFromCache(CACHE_KEY)).thenReturn(null);
        var existing = newUser(null);
        when(userRepository.findByEmailAndProvider(EMAIL, OAuth2Provider.KAKAO)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        var result = resolver.resolveAndCache(details(OAUTH_ID), ATTRIBUTES, USER_NAME_ATTRIBUTE);

        assertThat(result.user()).isSameAs(existing);
        assertThat(existing.getOauthId()).isEqualTo(OAUTH_ID); // 백필 확인
        verify(userRepository, times(1)).save(existing);
        verify(userCache, times(1)).putUserInCache(any(OAuth2User.class));
    }

    @Test
    @DisplayName("oauthId 가 이미 있는 기존 사용자는 백필되지 않음")
    void 기존_사용자_oauthId_존재() {
        when(userCache.getUserFromCache(CACHE_KEY)).thenReturn(null);
        var existing = newUser("기존_oauth_id");
        when(userRepository.findByEmailAndProvider(EMAIL, OAuth2Provider.KAKAO)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        resolver.resolveAndCache(details(OAUTH_ID), ATTRIBUTES, USER_NAME_ATTRIBUTE);

        assertThat(existing.getOauthId()).isEqualTo("기존_oauth_id"); // 덮어쓰이지 않음
    }

    @Test
    @DisplayName("캐시 키는 \"registrationId:email\" 포맷")
    void 캐시_키_포맷() {
        when(userCache.getUserFromCache(eq(CACHE_KEY))).thenReturn(null);
        when(userRepository.findByEmailAndProvider(EMAIL, OAuth2Provider.KAKAO)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        resolver.resolveAndCache(details(OAUTH_ID), ATTRIBUTES, USER_NAME_ATTRIBUTE);

        verify(userCache).getUserFromCache(CACHE_KEY);
    }

    private OAuth2UserDetails details(final String oauthId) {
        return new OAuth2UserDetails("이름", EMAIL, "p.jpg", "t.jpg", OAuth2Provider.KAKAO, oauthId);
    }

    private User newUser(final String oauthId) {
        return new User("이름", EMAIL, "p.jpg", "t.jpg", OAuth2Provider.KAKAO, oauthId);
    }
}
