package com.dnd.modutime.core.notification.application.command;

public record DeviceTokenRegisterCommand(
        String token,
        Long userId
) {
    public static DeviceTokenRegisterCommand of(String token, Long userId) {
        return new DeviceTokenRegisterCommand(token, userId);
    }
}
