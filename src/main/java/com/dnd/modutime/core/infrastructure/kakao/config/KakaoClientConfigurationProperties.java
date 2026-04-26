package com.dnd.modutime.core.infrastructure.kakao.config;

import com.dnd.modutime.core.infrastructure.common.ClientProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "clients.kakao")
public record KakaoClientConfigurationProperties(
        String clientMode,
        ClientProperties properties
) {
}
