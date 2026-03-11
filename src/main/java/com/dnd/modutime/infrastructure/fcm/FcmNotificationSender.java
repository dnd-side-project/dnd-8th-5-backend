package com.dnd.modutime.infrastructure.fcm;

import com.dnd.modutime.core.notification.domain.DeviceTokenRepository;
import com.dnd.modutime.core.notification.domain.NotificationSender;
import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class FcmNotificationSender implements NotificationSender {

    private final FirebaseMessaging firebaseMessaging;
    private final DeviceTokenRepository deviceTokenRepository;

    public FcmNotificationSender(FirebaseMessaging firebaseMessaging,
                                 DeviceTokenRepository deviceTokenRepository) {
        this.firebaseMessaging = firebaseMessaging;
        this.deviceTokenRepository = deviceTokenRepository;
    }

    @Override
    public void send(List<String> tokens, String title, String body, Map<String, String> data) {
        if (tokens.isEmpty()) {
            return;
        }

        var message = MulticastMessage.builder()
                .putAllData(data)
                .addAllTokens(tokens)
                .build();

        try {
            var response = firebaseMessaging.sendEachForMulticast(message);
            handleFailures(tokens, response);
        } catch (FirebaseMessagingException e) {
            log.warn("FCM 메시지 발송 실패: {}", e.getMessage());
        }
    }

    private void handleFailures(List<String> tokens, BatchResponse response) {
        if (response.getFailureCount() == 0) {
            return;
        }

        var invalidTokens = new ArrayList<String>();
        var responses = response.getResponses();
        for (int i = 0; i < responses.size(); i++) {
            if (!responses.get(i).isSuccessful()) {
                var exception = responses.get(i).getException();
                if (exception != null) {
                    var errorCode = exception.getMessagingErrorCode();
                    if (errorCode == MessagingErrorCode.UNREGISTERED
                            || errorCode == MessagingErrorCode.INVALID_ARGUMENT) {
                        invalidTokens.add(tokens.get(i));
                    }
                    log.warn("FCM 토큰 발송 실패 [{}]: {} ({})",
                            tokens.get(i), exception.getMessage(), errorCode);
                }
            }
        }

        for (var invalidToken : invalidTokens) {
            try {
                deviceTokenRepository.deleteByToken(invalidToken);
            } catch (Exception e) {
                log.warn("만료된 FCM 토큰 삭제 실패 [{}]: {}", invalidToken, e.getMessage());
            }
        }
    }
}
