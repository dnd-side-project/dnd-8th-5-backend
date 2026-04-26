package com.dnd.modutime.core.user.application;

import com.dnd.modutime.core.common.ErrorCode;
import com.dnd.modutime.core.user.UserNotFoundException;
import com.dnd.modutime.core.user.UserRepository;
import com.dnd.modutime.core.user.application.command.UserWithdrawCommand;
import com.dnd.modutime.util.TimeProvider;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserWithdrawCommandHandler {

    private final UserRepository userRepository;
    private final UserCache userCache;
    private final TimeProvider timeProvider;

    public UserWithdrawCommandHandler(final UserRepository userRepository,
                                      final UserCache userCache,
                                      final TimeProvider timeProvider) {
        this.userRepository = userRepository;
        this.userCache = userCache;
        this.timeProvider = timeProvider;
    }

    @Transactional
    public void handle(final UserWithdrawCommand command) {
        this.userCache.removeUserFromCache(command.getCacheKey());
        var user = this.userRepository.findById(command.getUserId())
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다.", ErrorCode.USER_NOT_FOUND));
        user.withdraw(this.timeProvider.getCurrentLocalDateTime());
    }
}
