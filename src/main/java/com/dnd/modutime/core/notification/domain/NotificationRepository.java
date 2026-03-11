package com.dnd.modutime.core.notification.domain;

public interface NotificationRepository {
    Notification save(Notification notification);

    <S extends Notification> java.util.List<S> saveAll(Iterable<S> notifications);
}
