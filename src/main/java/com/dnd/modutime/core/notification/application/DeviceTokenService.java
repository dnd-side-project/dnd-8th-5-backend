package com.dnd.modutime.core.notification.application;

import com.dnd.modutime.core.notification.application.command.DeviceTokenRegisterCommand;
import com.dnd.modutime.core.notification.domain.DeviceToken;
import com.dnd.modutime.core.notification.domain.DeviceTokenQueryRepository;
import com.dnd.modutime.core.notification.domain.DeviceTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeviceTokenService {

    private final DeviceTokenRepository deviceTokenRepository;
    private final DeviceTokenQueryRepository deviceTokenQueryRepository;

    public DeviceTokenService(DeviceTokenRepository deviceTokenRepository,
                              DeviceTokenQueryRepository deviceTokenQueryRepository) {
        this.deviceTokenRepository = deviceTokenRepository;
        this.deviceTokenQueryRepository = deviceTokenQueryRepository;
    }

    @Transactional
    public boolean register(DeviceTokenRegisterCommand command) {
        var existing = deviceTokenQueryRepository.findByToken(command.token());
        if (existing.isPresent()) {
            var deviceToken = existing.get();
            if (!deviceToken.getUserId().equals(command.userId())) {
                deviceTokenRepository.deleteByToken(command.token());
                deviceTokenRepository.save(DeviceToken.of(command.token(), command.userId()));
            }
            return false;
        }
        deviceTokenRepository.save(DeviceToken.of(command.token(), command.userId()));
        return true;
    }

    @Transactional
    public void unregister(String token, Long userId) {
        deviceTokenRepository.deleteByTokenAndUserId(token, userId);
    }
}
