package com.dnd.modutime.core.user.application;

import com.dnd.modutime.core.infrastructure.kakao.KakaoClient;
import com.dnd.modutime.core.infrastructure.kakao.KakaoException;
import com.dnd.modutime.core.user.OAuth2Provider;
import com.dnd.modutime.core.user.User;
import com.dnd.modutime.core.user.UserNotFoundException;
import com.dnd.modutime.core.user.UserRepository;
import com.dnd.modutime.util.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserCache;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@DisplayName("UserWithdrawService")
class UserWithdrawServiceTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 4, 26, 10, 0);

    private UserRepository userRepository;
    private KakaoClient kakaoClient;
    private UserCache userCache;
    private TimeProvider timeProvider;
    private UserWithdrawService service;

    @BeforeEach
    void setUp() {
        this.userRepository = mock(UserRepository.class);
        this.kakaoClient = mock(KakaoClient.class);
        this.userCache = mock(UserCache.class);
        this.timeProvider = mock(TimeProvider.class);
        when(this.timeProvider.getCurrentLocalDateTime()).thenReturn(NOW);

        this.service = new UserWithdrawService(userRepository, kakaoClient, userCache, timeProvider);
    }

    @Test
    @DisplayName("정상 탈퇴: 카카오 unlink → 캐시 제거 → soft delete")
    void 정상_탈퇴() {
        var user = newUser(1L, "test@example.com", OAuth2Provider.KAKAO, "12345");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        service.withdraw(1L);

        verify(kakaoClient, times(1)).unlinkByUserId("12345");
        verify(userCache, times(1)).removeUserFromCache("kakao:test@example.com");
        assertThat(user.isWithdrawn()).isTrue();
        assertThat(user.getDeletedAt()).isEqualTo(NOW);
    }

    @Test
    @DisplayName("사용자가 없으면 UserNotFoundException")
    void 사용자_없음() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.withdraw(999L))
                .isInstanceOf(UserNotFoundException.class);

        verify(kakaoClient, never()).unlinkByUserId(anyString());
    }

    @Test
    @DisplayName("oauthId가 null인 백필 전 사용자는 IllegalStateException")
    void oauthId_없음_거부() {
        var user = newUser(1L, "legacy@example.com", OAuth2Provider.KAKAO, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> service.withdraw(1L))
                .isInstanceOf(IllegalStateException.class);

        verify(kakaoClient, never()).unlinkByUserId(anyString());
        verify(userCache, never()).removeUserFromCache(anyString());
        assertThat(user.isWithdrawn()).isFalse();
    }

    @Test
    @DisplayName("카카오 unlink 실패 시 예외 전파, DB는 변경되지 않음")
    void 카카오_실패시_DB_변경없음() {
        var user = newUser(1L, "test@example.com", OAuth2Provider.KAKAO, "12345");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doThrow(new KakaoException.KakaoServerException("카카오 장애"))
                .when(kakaoClient).unlinkByUserId("12345");

        assertThatThrownBy(() -> service.withdraw(1L))
                .isInstanceOf(KakaoException.KakaoServerException.class);

        verify(userCache, never()).removeUserFromCache(anyString());
        assertThat(user.isWithdrawn()).isFalse();
    }

    @Test
    @DisplayName("이미 탈퇴된 사용자는 멱등 처리 (예외 없음)")
    void 이미_탈퇴된_사용자_멱등() {
        var user = newUser(1L, "test@example.com", OAuth2Provider.KAKAO, "12345");
        user.withdraw(NOW.minusDays(1));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        service.withdraw(1L);

        verify(kakaoClient, never()).unlinkByUserId(anyString());
        verify(userCache, never()).removeUserFromCache(any());
    }

    private User newUser(final Long id, final String email, final OAuth2Provider provider, final String oauthId) {
        var user = new User("이름", email, "p.jpg", "t.jpg", provider, oauthId);
        setId(user, id);
        return user;
    }

    private void setId(final Object entity, final Long id) {
        try {
            Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
