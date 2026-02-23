package com.dnd.modutime.core.room.controller;

import com.dnd.modutime.core.adjustresult.domain.AdjustmentResult;
import com.dnd.modutime.core.adjustresult.repository.AdjustmentResultRepository;
import com.dnd.modutime.core.common.ModutimeHostConfigurationProperties;
import com.dnd.modutime.core.room.domain.Room;
import com.dnd.modutime.core.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor
public class OpenGraphController {

    private final RoomRepository roomRepository;
    private final AdjustmentResultRepository adjustmentResultRepository;
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
        var startDate = adjustmentResultRepository.findByRoomUuid(roomUuid)
                .map(AdjustmentResult::getCandidateDateTimes)
                .filter(candidates -> !candidates.isEmpty())
                .map(candidates -> candidates.get(0).getStartDateTime())
                .orElse(null);
        var html = buildRoomHtml(room, inviteUrl, startDate);

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

    private String buildRoomHtml(Room room, String inviteUrl, LocalDateTime startDate) {
        var title = escapeHtml(room.getTitle() + " - 모두의시간");
        var description = startDate != null
                ? escapeHtml(formatKoreanDate(startDate) + "에 만나요")
                : escapeHtml("일정을 등록하고 최적의 시간을 찾아보세요!");
        var escapedInviteUrl = escapeHtml(inviteUrl);
        var jsonLdBlock = buildJsonLdBlock(room, inviteUrl, startDate);

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

    private String buildJsonLdBlock(Room room, String inviteUrl, LocalDateTime startDate) {
        if (startDate == null) {
            return "";
        }

        var eventName = escapeJson(room.getTitle());
        var startDateStr = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        return String.format("""
                <script type="application/ld+json">
                {
                  "@context": "https://schema.org",
                  "@type": "Event",
                  "name": "%s",
                  "startDate": "%s",
                  "url": "%s",
                  "eventAttendanceMode": "https://schema.org/OfflineEventAttendanceMode"
                }
                </script>
                """, eventName, startDateStr, inviteUrl);
    }

    private String formatKoreanDate(LocalDateTime dateTime) {
        return dateTime.getMonthValue() + "월 " + dateTime.getDayOfMonth() + "일";
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
