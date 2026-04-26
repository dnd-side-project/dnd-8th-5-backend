package com.dnd.modutime.core.user.application;

import com.dnd.modutime.core.infrastructure.kakao.KakaoClient;
import com.dnd.modutime.core.infrastructure.kakao.KakaoException;
import com.dnd.modutime.core.user.InsufficientAuthenticationException;
import com.dnd.modutime.core.user.OAuth2Provider;
import com.dnd.modutime.core.user.User;
import com.dnd.modutime.core.user.UserNotFoundException;
import com.dnd.modutime.core.user.UserRepository;
import com.dnd.modutime.core.user.application.command.UserWithdrawCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@DisplayName("UserWithdrawFacade")
class UserWithdrawFacadeTest {

    private UserRepository userRepository;
    private KakaoClient kakaoClient;
    private UserWithdrawCommandHandler commandHandler;
    private UserWithdrawFacade facade;

    @BeforeEach
    void setUp() {
        this.userRepository = mock(UserRepository.class);
        this.kakaoClient = mock(KakaoClient.class);
        this.commandHandler = mock(UserWithdrawCommandHandler.class);

        this.facade = new UserWithdrawFacade(userRepository, kakaoClient, commandHandler);
    }

    @Test
    @DisplayName("정상 탈퇴: 카카오 unlink → CommandHandler 위임 (호출 순서 보장)")
    void 정상_탈퇴() {
        var user = newUser(1L, "test@example.com", OAuth2Provider.KAKAO, "12345");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        facade.withdraw(1L);

        InOrder order = inOrder(kakaoClient, commandHandler);
        order.verify(kakaoClient, times(1)).unlinkByUserId("12345");

        ArgumentCaptor<UserWithdrawCommand> captor = ArgumentCaptor.forClass(UserWithdrawCommand.class);
        order.verify(commandHandler, times(1)).handle(captor.capture());

        var command = captor.getValue();
        assertThat(command.getUserId()).isEqualTo(1L);
        assertThat(command.getCacheKey()).isEqualTo("kakao:test@example.com");
    }

    @Test
    @DisplayName("사용자가 없으면 UserNotFoundException — 외부 API/CommandHandler 호출 없음")
    void 사용자_없음() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> facade.withdraw(999L))
                .isInstanceOf(UserNotFoundException.class);

        verify(kakaoClient, never()).unlinkByUserId(anyString());
        verify(commandHandler, never()).handle(any());
    }

    @Test
    @DisplayName("oauthId가 null이면 InsufficientAuthenticationException — 외부 API/CommandHandler 호출 없음")
    void oauthId_없음_거부() {
        var user = newUser(1L, "legacy@example.com", OAuth2Provider.KAKAO, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> facade.withdraw(1L))
                .isInstanceOf(InsufficientAuthenticationException.class);

        verify(kakaoClient, never()).unlinkByUserId(anyString());
        verify(commandHandler, never()).handle(any());
    }

    @Test
    @DisplayName("카카오 unlink 실패 시 예외 전파, CommandHandler 호출 안 됨")
    void 카카오_실패시_CommandHandler_미호출() {
        var user = newUser(1L, "test@example.com", OAuth2Provider.KAKAO, "12345");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doThrow(new KakaoException.KakaoServerException("카카오 장애"))
                .when(kakaoClient).unlinkByUserId("12345");

        assertThatThrownBy(() -> facade.withdraw(1L))
                .isInstanceOf(KakaoException.KakaoServerException.class);

        verify(commandHandler, never()).handle(any());
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
