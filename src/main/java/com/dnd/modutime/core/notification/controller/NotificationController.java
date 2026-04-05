package com.dnd.modutime.core.notification.controller;

import com.dnd.modutime.core.auth.oauth.OAuth2User;
import com.dnd.modutime.core.notification.application.NotificationQueryService;
import com.dnd.modutime.core.notification.application.response.NotificationResponse;
import com.dnd.modutime.core.notification.application.response.UnreadCountResponse;
import com.dnd.modutime.infrastructure.PageRequest;
import com.dnd.modutime.infrastructure.PageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

    private final NotificationQueryService notificationQueryService;

    public NotificationController(NotificationQueryService notificationQueryService) {
        this.notificationQueryService = notificationQueryService;
    }

    @GetMapping("/api/v1/notifications")
    public ResponseEntity<PageResponse<NotificationResponse>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal OAuth2User oAuth2User) {
        var pageRequest = PageRequest.of(page, size);
        var response = notificationQueryService.getNotifications(oAuth2User.user().getId(), pageRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/v1/notifications/unread-count")
    public ResponseEntity<UnreadCountResponse> getUnreadCount(
            @AuthenticationPrincipal OAuth2User oAuth2User) {
        var response = notificationQueryService.getUnreadCount(oAuth2User.user().getId());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/api/v1/notifications/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal OAuth2User oAuth2User) {
        notificationQueryService.markAsRead(notificationId, oAuth2User.user().getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/api/v1/notifications/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @AuthenticationPrincipal OAuth2User oAuth2User) {
        notificationQueryService.markAllAsRead(oAuth2User.user().getId());
        return ResponseEntity.noContent().build();
    }
}
