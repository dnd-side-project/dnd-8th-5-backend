package com.dnd.modutime.infrastructure.fcm;

import com.dnd.modutime.core.notification.domain.DeviceTokenRepository;
import com.dnd.modutime.core.notification.domain.NotificationSender;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FcmConfig {

    @Bean
    @ConditionalOnProperty(name = "fcm.enabled", havingValue = "true")
    public NotificationSender fcmNotificationSender(
            @Value("${fcm.service-account-path}") String serviceAccountPath,
            DeviceTokenRepository deviceTokenRepository) throws IOException {
        FirebaseOptions options;
        try (var serviceAccount = new FileInputStream(serviceAccountPath)) {
            options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
        }

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }

        return new FcmNotificationSender(FirebaseMessaging.getInstance(), deviceTokenRepository);
    }

    @Bean
    @ConditionalOnProperty(name = "fcm.enabled", havingValue = "false", matchIfMissing = true)
    public NotificationSender noOpNotificationSender() {
        return new NoOpNotificationSender();
    }
}
