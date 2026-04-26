package com.dnd.modutime.core.user.application.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserWithdrawCommand {

    private Long userId;
    private String cacheKey;
    private String reason;

    public static UserWithdrawCommand of(final Long userId, final String cacheKey, final String reason) {
        var command = new UserWithdrawCommand();
        command.userId = userId;
        command.cacheKey = cacheKey;
        command.reason = reason;
        return command;
    }
}
