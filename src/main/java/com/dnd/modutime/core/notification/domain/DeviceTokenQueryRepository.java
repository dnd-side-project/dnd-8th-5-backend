package com.dnd.modutime.core.notification.domain;

import java.util.List;
import java.util.Optional;

public interface DeviceTokenQueryRepository {
    List<DeviceToken> findByUserId(Long userId);

    List<DeviceToken> findByUserIdIn(List<Long> userIds);

    Optional<DeviceToken> findByToken(String token);

    boolean existsByToken(String token);
}
