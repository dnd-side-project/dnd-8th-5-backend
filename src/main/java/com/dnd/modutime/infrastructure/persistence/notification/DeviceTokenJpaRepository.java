package com.dnd.modutime.infrastructure.persistence.notification;

import com.dnd.modutime.core.notification.domain.DeviceToken;
import com.dnd.modutime.core.notification.domain.DeviceTokenRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceTokenJpaRepository extends JpaRepository<DeviceToken, Long>,
        DeviceTokenRepository {
}
