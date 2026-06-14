package com.dnd.modutime.core.feedback.domain;

/**
 * 피드백 유형. 프론트(FeedbackBottomSheet)가 설계한 5종이며, 변경 시 프론트와 합의한다.
 */
public enum FeedbackCategory {
    PRAISE,
    REVIEW,
    FEATURE,
    QUESTION,
    BUG,
}
