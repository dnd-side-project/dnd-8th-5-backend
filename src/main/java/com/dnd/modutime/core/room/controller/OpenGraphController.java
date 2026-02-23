package com.dnd.modutime.core.room.controller;

import com.dnd.modutime.core.common.ModutimeHostConfigurationProperties;
import com.dnd.modutime.core.room.domain.Room;
import com.dnd.modutime.core.room.domain.RoomDate;
import com.dnd.modutime.core.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

@Controller
@RequiredArgsConstructor
public class OpenGraphController {

    private final RoomRepository roomRepository;
    private final ModutimeHostConfigurationProperties hostProperties;

    @GetMapping(value = "/og/invite/{roomUuid}", produces = "text/html; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<String> getOpenGraphHtml(@PathVariable String roomUuid) {
        var roomOptional = roomRepository.findByUuid(roomUuid);

        if (roomOptional.isEmpty()) {
            return ResponseEntity.ok(buildDefaultHtml());
        }

        var room = roomOptional.get();
        var inviteUrl = hostProperties.host().client() + "/result/" + roomUuid;
        var html = buildRoomHtml(room, inviteUrl);

        return ResponseEntity.ok(html);
    }

    private String buildDefaultHtml() {
        var clientUrl = hostProperties.host().client();
        var title = escapeHtml("모두의시간");
        var description = escapeHtml("함께 최적의 모임 시간을 찾아보세요!");

        return String.format("""
                <!DOCTYPE html>
                <html lang="ko">
                <head>
                    <meta charset="UTF-8">
                    <meta property="og:title" content="%s">
                    <meta property="og:description" content="%s">
                    <meta property="og:type" content="website">
                    <meta property="og:url" content="%s">
                    <meta http-equiv="refresh" content="0;url=%s">
                    <title>%s</title>
                </head>
                <body></body>
                </html>
                """, title, description, clientUrl, clientUrl, title);
    }

    private String buildRoomHtml(Room room, String inviteUrl) {
        var title = escapeHtml(room.getTitle() + " - 모두의시간");
        var description = escapeHtml("이 시간에 만나요");
        var escapedInviteUrl = escapeHtml(inviteUrl);
        var jsonLdBlock = buildJsonLdBlock(room, inviteUrl);

        return String.format("""
                <!DOCTYPE html>
                <html lang="ko">
                <head>
                    <meta charset="UTF-8">
                    <meta property="og:title" content="%s">
                    <meta property="og:description" content="%s">
                    <meta property="og:type" content="website">
                    <meta property="og:url" content="%s">
                    <meta http-equiv="refresh" content="0;url=%s">
                    <title>%s</title>
                    %s
                </head>
                <body></body>
                </html>
                """, title, description, escapedInviteUrl, escapedInviteUrl, title, jsonLdBlock);
    }

    private String buildJsonLdBlock(Room room, String inviteUrl) {
        var roomDates = room.getRoomDates();

        if (roomDates.isEmpty() || !room.hasStartAndEndTime()) {
            return "";
        }

        var firstDate = roomDates.stream()
                .map(RoomDate::getDate)
                .min(Comparator.naturalOrder())
                .orElse(null);

        var lastDate = roomDates.stream()
                .map(RoomDate::getDate)
                .max(Comparator.naturalOrder())
                .orElse(null);

        if (firstDate == null || lastDate == null) {
            return "";
        }

        var startTime = room.getStartTimeOrNull();
        var endTime = room.getEndTimeOrNull();
        var startDateTime = formatDateTime(firstDate, startTime);
        var endDateTime = formatDateTime(lastDate, endTime);
        var eventName = escapeJson(room.getTitle());

        return String.format("""
                <script type="application/ld+json">
                {
                  "@context": "https://schema.org",
                  "@type": "Event",
                  "name": "%s",
                  "startDate": "%s",
                  "endDate": "%s",
                  "url": "%s",
                  "eventAttendanceMode": "https://schema.org/OfflineEventAttendanceMode"
                }
                </script>
                """, eventName, startDateTime, endDateTime, inviteUrl);
    }

    private String formatDateTime(LocalDate date, LocalTime time) {
        var dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        var timeStr = time.format(DateTimeFormatter.ISO_LOCAL_TIME);
        return dateStr + "T" + timeStr;
    }

    private String escapeHtml(String input) {
        if (input == null) {
            return "";
        }
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String escapeJson(String input) {
        if (input == null) {
            return "";
        }
        return input
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
