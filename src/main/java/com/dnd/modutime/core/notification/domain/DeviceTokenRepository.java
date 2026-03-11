package com.dnd.modutime.core.notification.domain;

public interface DeviceTokenRepository {
    DeviceToken save(DeviceToken deviceToken);

    void deleteByTokenAndUserId(String token, Long userId);

    void deleteByToken(String token);
}
