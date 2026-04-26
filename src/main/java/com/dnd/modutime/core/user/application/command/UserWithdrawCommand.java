package com.dnd.modutime.core.user.application.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserWithdrawCommand {

    private Long userId;
    private String cacheKey;

    public static UserWithdrawCommand of(final Long userId, final String cacheKey) {
        var command = new UserWithdrawCommand();
        command.userId = userId;
        command.cacheKey = cacheKey;
        return command;
    }
}
