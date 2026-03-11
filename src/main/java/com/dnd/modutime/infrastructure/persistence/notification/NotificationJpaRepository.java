package com.dnd.modutime.infrastructure.persistence.notification;

import com.dnd.modutime.core.notification.domain.Notification;
import com.dnd.modutime.core.notification.domain.NotificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationJpaRepository extends JpaRepository<Notification, Long>,
        NotificationRepository {
}
