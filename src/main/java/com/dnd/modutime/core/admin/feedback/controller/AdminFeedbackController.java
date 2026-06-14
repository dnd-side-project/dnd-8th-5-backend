package com.dnd.modutime.core.admin.feedback.controller;

import com.dnd.modutime.core.admin.feedback.application.AdminFeedbackService;
import com.dnd.modutime.core.admin.feedback.application.response.FeedbackDetailResponse;
import com.dnd.modutime.core.admin.feedback.application.response.FeedbackRowResponse;
import com.dnd.modutime.core.admin.feedback.controller.dto.FeedbackTriageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 어드민 "버그제보" 메뉴용 피드백 API.
 *
 * <p>{@code /admin/**} 경로는 {@code AdminSecurityConfig} 체인이 어드민 access token 인증을 강제하므로
 * 별도 인증 애너테이션은 필요 없다.</p>
 */
@RestController
@RequiredArgsConstructor
public class AdminFeedbackController {

    private final AdminFeedbackService adminFeedbackService;

    @GetMapping("/admin/api/v1/feedback")
    public List<FeedbackRowResponse> getList() {
        return adminFeedbackService.getList();
    }

    @GetMapping("/admin/api/v1/feedback/{id}")
    public FeedbackDetailResponse getDetail(@PathVariable Long id) {
        return adminFeedbackService.getDetail(id);
    }

    @PatchMapping("/admin/api/v1/feedback/{id}")
    public FeedbackRowResponse triage(@PathVariable Long id, @RequestBody FeedbackTriageRequest request) {
        return adminFeedbackService.triage(id, request.toCommand());
    }
}
