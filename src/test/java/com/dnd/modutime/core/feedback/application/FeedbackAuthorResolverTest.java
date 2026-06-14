package com.dnd.modutime.core.feedback.application;

import com.dnd.modutime.core.auth.oauth.OAuth2User;
import com.dnd.modutime.core.auth.oauth.exception.InvalidOAuth2TokenException;
import com.dnd.modutime.core.auth.oauth.facade.OAuth2TokenProvider;
import com.dnd.modutime.core.common.ErrorCode;
import com.dnd.modutime.core.feedback.application.command.FeedbackAuthor;
import com.dnd.modutime.core.feedback.domain.AuthorType;
import com.dnd.modutime.core.user.OAuth2Provider;
import com.dnd.modutime.core.user.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedbackAuthorResolverTest {

    @Mock
    private OAuth2TokenProvider oAuth2TokenProvider;

    @InjectMocks
    private FeedbackAuthorResolver resolver;

    @DisplayName("Authorization 헤더가 없으면 익명으로 판정한다")
    @Test
    void 헤더없음_익명() {
        var request = new MockHttpServletRequest();

        FeedbackAuthor author = resolver.resolve(request);

        assertThat(author.type()).isEqualTo(AuthorType.ANONYMOUS);
        assertThat(author.userId()).isNull();
    }

    @DisplayName("guest 토큰이면 GUEST로 판정하고 참여자명을 추출한다")
    @Test
    void guest토큰_게스트() {
        var request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer guest.token");
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("김모두");
        when(oAuth2TokenProvider.isGuestToken("guest.token")).thenReturn(true);
        when(oAuth2TokenProvider.getOAuth2TokenClaims("guest.token")).thenReturn(claims);

        FeedbackAuthor author = resolver.resolve(request);

        assertThat(author.type()).isEqualTo(AuthorType.GUEST);
        assertThat(author.name()).isEqualTo("김모두");
        assertThat(author.userId()).isNull();
    }

    @DisplayName("유효한 카카오 토큰이면 MEMBER로 판정하고 userId/email을 추출한다")
    @Test
    void 카카오토큰_회원() {
        var request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer kakao.token");

        User user = new User("김카카오", "kakao@example.com", "p", "t", OAuth2Provider.KAKAO);
        ReflectionTestUtils.setField(user, "id", 42L);
        var principal = new OAuth2User(user, Map.of("sub", "kakao:kakao@example.com"), "sub");
        var authentication = new OAuth2AuthenticationToken(principal, List.of(), "kakao");

        when(oAuth2TokenProvider.isGuestToken("kakao.token")).thenReturn(false);
        when(oAuth2TokenProvider.validateOAuth2Token("kakao.token")).thenReturn(true);
        when(oAuth2TokenProvider.getAuthentication("kakao.token")).thenReturn(authentication);

        FeedbackAuthor author = resolver.resolve(request);

        assertThat(author.type()).isEqualTo(AuthorType.MEMBER);
        assertThat(author.userId()).isEqualTo(42L);
        assertThat(author.name()).isEqualTo("김카카오");
        assertThat(author.email()).isEqualTo("kakao@example.com");
    }

    @DisplayName("만료/무효 토큰이면 401 대신 익명으로 강등한다")
    @Test
    void 무효토큰_익명강등() {
        var request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid.token");
        when(oAuth2TokenProvider.isGuestToken("invalid.token")).thenReturn(false);
        when(oAuth2TokenProvider.validateOAuth2Token("invalid.token"))
                .thenThrow(new InvalidOAuth2TokenException("유효하지 않은 토큰", ErrorCode.INVALID_TOKEN));

        FeedbackAuthor author = resolver.resolve(request);

        assertThat(author.type()).isEqualTo(AuthorType.ANONYMOUS);
    }

    @DisplayName("Bearer 형식이 아니면 익명으로 판정한다")
    @Test
    void 비_Bearer_익명() {
        var request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic abcdef");

        FeedbackAuthor author = resolver.resolve(request);

        assertThat(author.type()).isEqualTo(AuthorType.ANONYMOUS);
    }
}
