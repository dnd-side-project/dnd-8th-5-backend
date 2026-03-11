package com.dnd.modutime.infrastructure.persistence.notification;

import com.dnd.modutime.core.notification.domain.DeviceToken;
import com.dnd.modutime.core.notification.domain.DeviceTokenQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceTokenJpaQueryRepository extends JpaRepository<DeviceToken, Long>,
        DeviceTokenQueryRepository {

    List<DeviceToken> findByUserId(Long userId);

    List<DeviceToken> findByUserIdIn(List<Long> userIds);

    Optional<DeviceToken> findByToken(String token);

    boolean existsByToken(String token);
}
