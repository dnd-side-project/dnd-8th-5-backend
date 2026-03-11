package com.dnd.modutime.infrastructure.persistence.notification;

import com.dnd.modutime.core.notification.domain.Notification;
import com.dnd.modutime.core.notification.domain.NotificationQueryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class NotificationQueryRepositoryImpl implements NotificationQueryRepository {

    private final NotificationJpaQueryRepository jpaQueryRepository;

    public NotificationQueryRepositoryImpl(NotificationJpaQueryRepository jpaQueryRepository) {
        this.jpaQueryRepository = jpaQueryRepository;
    }

    @Override
    public List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId, int offset, int limit) {
        var pageable = PageRequest.of(offset / limit, limit);
        return jpaQueryRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId, pageable);
    }

    @Override
    public long countByRecipientId(Long recipientId) {
        return jpaQueryRepository.countByRecipientId(recipientId);
    }

    @Override
    public long countByRecipientIdAndReadFalse(Long recipientId) {
        return jpaQueryRepository.countByRecipientIdAndReadFalse(recipientId);
    }

    @Override
    public Optional<Notification> findById(Long id) {
        return jpaQueryRepository.findById(id);
    }

    @Override
    public Optional<Notification> findByIdAndRecipientId(Long id, Long recipientId) {
        return jpaQueryRepository.findByIdAndRecipientId(id, recipientId);
    }

    @Override
    @Transactional
    public void markAllAsReadByRecipientId(Long recipientId) {
        jpaQueryRepository.markAllAsReadByRecipientId(recipientId);
    }
}
