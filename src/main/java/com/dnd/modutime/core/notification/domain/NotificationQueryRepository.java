package com.dnd.modutime.core.notification.domain;

import java.util.List;
import java.util.Optional;

public interface NotificationQueryRepository {
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId, int offset, int limit);

    long countByRecipientId(Long recipientId);

    long countByRecipientIdAndReadFalse(Long recipientId);

    Optional<Notification> findById(Long id);

    Optional<Notification> findByIdAndRecipientId(Long id, Long recipientId);

    void markAllAsReadByRecipientId(Long recipientId);
}
