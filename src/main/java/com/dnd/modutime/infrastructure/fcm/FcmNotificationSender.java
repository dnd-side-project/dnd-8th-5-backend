package com.dnd.modutime.infrastructure.fcm;

import com.dnd.modutime.core.notification.domain.DeviceTokenRepository;
import com.dnd.modutime.core.notification.domain.NotificationSendResult;
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
    public NotificationSendResult send(List<String> tokens, String title, String body, Map<String, String> data) {
        if (tokens.isEmpty()) {
            return NotificationSendResult.empty();
        }

        var notification = com.google.firebase.messaging.Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        var message = MulticastMessage.builder()
                .setNotification(notification)
                .putAllData(data)
                .addAllTokens(tokens)
                .build();

        try {
            var response = firebaseMessaging.sendEachForMulticast(message);
            var failedTokens = handleFailures(tokens, response);
            var successCount = response.getSuccessCount();
            var failureCount = response.getFailureCount();

            if (failureCount > 0) {
                return NotificationSendResult.failure(successCount, failureCount, failedTokens);
            }
            return NotificationSendResult.success(successCount);
        } catch (FirebaseMessagingException e) {
            log.warn("FCM 메시지 발송 실패: {}", e.getMessage());
            return NotificationSendResult.failure(0, tokens.size(), tokens);
        }
    }

    private List<String> handleFailures(List<String> tokens, BatchResponse response) {
        var failedTokens = new ArrayList<String>();
        if (response.getFailureCount() == 0) {
            return failedTokens;
        }

        var invalidTokens = new ArrayList<String>();
        var responses = response.getResponses();
        for (int i = 0; i < responses.size(); i++) {
            if (!responses.get(i).isSuccessful()) {
                failedTokens.add(tokens.get(i));
                var exception = responses.get(i).getException();
                if (exception != null) {
                    var errorCode = exception.getMessagingErrorCode();
                    if (errorCode == MessagingErrorCode.UNREGISTERED
                            || errorCode == MessagingErrorCode.INVALID_ARGUMENT) {
                        invalidTokens.add(tokens.get(i));
                    }
                    log.warn("FCM 토큰 발송 실패 [{}]: {} ({})",
                            maskToken(tokens.get(i)), exception.getMessage(), errorCode);
                }
            }
        }

        for (var invalidToken : invalidTokens) {
            try {
                deviceTokenRepository.deleteByToken(invalidToken);
            } catch (Exception e) {
                log.warn("만료된 FCM 토큰 삭제 실패 [{}]: {}", maskToken(invalidToken), e.getMessage());
            }
        }

        return failedTokens;
    }

    private String maskToken(String token) {
        if (token == null || token.length() <= 8) {
            return "****";
        }
        return token.substring(0, 4) + "..." + token.substring(token.length() - 4);
    }
}
