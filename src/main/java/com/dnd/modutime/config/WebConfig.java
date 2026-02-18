package com.dnd.modutime.config;

import com.dnd.modutime.core.auth.application.GuestParticipantArgumentResolver;
import com.dnd.modutime.core.auth.application.RoomParticipantArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    public static final String ALLOWED_METHOD_NAMES = "GET,HEAD,POST,PUT,DELETE,TRACE,OPTIONS,PATCH";

    private final GuestParticipantArgumentResolver guestParticipantArgumentResolver;
    private final RoomParticipantArgumentResolver roomParticipantArgumentResolver;

    public WebConfig(GuestParticipantArgumentResolver guestParticipantArgumentResolver,
                     RoomParticipantArgumentResolver roomParticipantArgumentResolver) {
        this.guestParticipantArgumentResolver = guestParticipantArgumentResolver;
        this.roomParticipantArgumentResolver = roomParticipantArgumentResolver;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedMethods(ALLOWED_METHOD_NAMES.split(","))
                .exposedHeaders(HttpHeaders.LOCATION);
        registry.addMapping("/oauth2/**")
                .allowedOrigins("*")
                .allowedMethods(ALLOWED_METHOD_NAMES.split(","))
                .exposedHeaders(HttpHeaders.LOCATION); // OAuth2 인증
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(guestParticipantArgumentResolver);
        resolvers.add(roomParticipantArgumentResolver);
    }
}
