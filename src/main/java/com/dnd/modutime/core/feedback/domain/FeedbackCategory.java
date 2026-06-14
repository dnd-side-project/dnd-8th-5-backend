package com.dnd.modutime.core.feedback.domain;

/**
 * 피드백 유형. 프론트(FeedbackBottomSheet)가 설계한 5종이며, 변경 시 프론트와 합의한다.
 */
public enum FeedbackCategory {
    PRAISE("칭찬"),
    REVIEW("후기"),
    FEATURE("기능 제안"),
    QUESTION("문의"),
    BUG("버그 제보"),
    ;

    /** 한국어 설명(가독성용 메타데이터). 현재 직렬화/로직에는 사용되지 않는다. */
    private final String description;

    FeedbackCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
