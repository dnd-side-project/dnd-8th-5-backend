package com.dnd.modutime.core.user.application;

import com.dnd.modutime.core.user.OAuth2Provider;
import com.dnd.modutime.core.user.User;
import com.dnd.modutime.core.user.UserNotFoundException;
import com.dnd.modutime.core.user.UserRepository;
import com.dnd.modutime.core.user.application.command.UserWithdrawCommand;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@DisplayName("UserWithdrawCommandHandler")
class UserWithdrawCommandHandlerTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 4, 26, 10, 0);
    private static final String REASON = "자주 사용하지 않아요";

    private UserRepository userRepository;
    private UserCache userCache;
    private TimeProvider timeProvider;
    private UserWithdrawCommandHandler handler;

    @BeforeEach
    void setUp() {
        this.userRepository = mock(UserRepository.class);
        this.userCache = mock(UserCache.class);
        this.timeProvider = mock(TimeProvider.class);
        when(this.timeProvider.getCurrentLocalDateTime()).thenReturn(NOW);

        this.handler = new UserWithdrawCommandHandler(userRepository, userCache, timeProvider);
    }

    @Test
    @DisplayName("캐시 무효화 후 user soft delete + 사유/동의시각 기록")
    void 정상_처리() {
        var user = newUser(1L, "test@example.com", OAuth2Provider.KAKAO, "12345");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        handler.handle(UserWithdrawCommand.of(1L, "kakao:test@example.com", REASON));

        verify(userCache, times(1)).removeUserFromCache("kakao:test@example.com");
        assertThat(user.isWithdrawn()).isTrue();
        assertThat(user.getDeletedAt()).isEqualTo(NOW);
        assertThat(user.getWithdrawReason()).isEqualTo(REASON);
        assertThat(user.getWithdrawConsentedAt()).isEqualTo(NOW);
    }

    @Test
    @DisplayName("user가 없으면 UserNotFoundException (이전 단계의 트랜잭션 외부 unlink 후 동시성 등으로 사라진 경우)")
    void 사용자_없음() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(UserWithdrawCommand.of(99L, "kakao:gone@example.com", REASON)))
                .isInstanceOf(UserNotFoundException.class);
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
