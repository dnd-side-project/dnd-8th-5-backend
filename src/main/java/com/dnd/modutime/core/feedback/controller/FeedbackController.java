package com.dnd.modutime.core.feedback.controller;

import com.dnd.modutime.core.feedback.application.FeedbackAuthorResolver;
import com.dnd.modutime.core.feedback.application.FeedbackService;
import com.dnd.modutime.core.feedback.application.command.FeedbackAuthor;
import com.dnd.modutime.core.feedback.application.response.FeedbackResponse;
import com.dnd.modutime.core.feedback.controller.dto.FeedbackRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final FeedbackAuthorResolver feedbackAuthorResolver;

    /**
     * 인앱 피드백을 제출한다. 카카오 로그인/비회원 로그인(guest token)/완전 비로그인 모두 허용한다(optional auth).
     */
    @PostMapping("/api/v1/feedback")
    public ResponseEntity<FeedbackResponse> create(@RequestBody @Valid FeedbackRequest request,
                                                   HttpServletRequest httpRequest) {
        FeedbackAuthor author = feedbackAuthorResolver.resolve(httpRequest);
        Long id = feedbackService.create(request.toCommand(author));
        return ResponseEntity.ok(new FeedbackResponse(id));
    }
}
