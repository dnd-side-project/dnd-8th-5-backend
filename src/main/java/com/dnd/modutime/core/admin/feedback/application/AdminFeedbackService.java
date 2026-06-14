package com.dnd.modutime.core.admin.feedback.application;

import com.dnd.modutime.core.admin.feedback.application.command.FeedbackTriageCommand;
import com.dnd.modutime.core.admin.feedback.application.response.FeedbackDetailResponse;
import com.dnd.modutime.core.admin.feedback.application.response.FeedbackRowResponse;
import com.dnd.modutime.core.admin.feedback.domain.FeedbackTriage;
import com.dnd.modutime.core.admin.feedback.repository.FeedbackTriageRepository;
import com.dnd.modutime.core.feedback.domain.Feedback;
import com.dnd.modutime.core.feedback.repository.FeedbackRepository;
import com.dnd.modutime.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 어드민 피드백(버그제보) 목록/상세/트리아지 유스케이스.
 *
 * <p>제출 데이터는 기존 {@link FeedbackRepository} 를 재사용해 읽고, 어드민 처리 상태는
 * {@link FeedbackTriage} 로 분리 관리한다. 트리아지 레코드는 첫 PATCH 때 생성된다(lazy).</p>
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminFeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final FeedbackTriageRepository feedbackTriageRepository;

    /**
     * 전건 목록 (snapshot 제외). 제출 시각 최신순으로 정렬한다.
     */
    public List<FeedbackRowResponse> getList() {
        var feedbacks = feedbackRepository.findAll();
        if (feedbacks.isEmpty()) {
            return List.of();
        }
        var triageByFeedbackId = loadTriages(feedbacks);
        return feedbacks.stream()
                .sorted(Comparator.comparing(Feedback::getCreatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(feedback -> FeedbackRowResponse.of(feedback, triageByFeedbackId.get(feedback.getId())))
                .toList();
    }

    /**
     * 단건 상세 (snapshot 포함). 존재하지 않으면 404.
     */
    public FeedbackDetailResponse getDetail(Long id) {
        var feedback = findFeedback(id);
        var triage = feedbackTriageRepository.findByFeedbackId(id).orElse(null);
        return FeedbackDetailResponse.of(feedback, triage);
    }

    /**
     * 심각도/상태 트리아지(부분 수정). 트리아지 레코드가 없으면 기본값으로 생성 후 적용한다.
     */
    @Transactional
    public FeedbackRowResponse triage(Long id, FeedbackTriageCommand command) {
        if (command.isEmpty()) {
            throw new IllegalArgumentException("변경할 severity 또는 status 중 하나는 필수입니다.");
        }
        var feedback = findFeedback(id);
        var triage = feedbackTriageRepository.findByFeedbackId(id)
                .orElseGet(() -> FeedbackTriage.initial(id));
        triage.applyTriage(command.severity(), command.status());
        var saved = feedbackTriageRepository.save(triage);
        return FeedbackRowResponse.of(feedback, saved);
    }

    private Feedback findFeedback(Long id) {
        return feedbackRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 피드백을 찾을 수 없습니다. id=" + id));
    }

    private Map<Long, FeedbackTriage> loadTriages(List<Feedback> feedbacks) {
        var feedbackIds = feedbacks.stream()
                .map(Feedback::getId)
                .toList();
        return feedbackTriageRepository.findByFeedbackIdIn(feedbackIds).stream()
                .collect(Collectors.toMap(FeedbackTriage::getFeedbackId, Function.identity()));
    }
}
