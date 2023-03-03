package com.dnd.modutime.core.adjustresult.application;

import java.util.Arrays;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CandidateDateTimeSortStandard {
    FAST("fast"), LONG("long");

    private final String value;

    public static CandidateDateTimeSortStandard getByValue(String value) {
        return Arrays.stream(CandidateDateTimeSortStandard.values())
                .filter(candidateDateTimeSortStandard -> candidateDateTimeSortStandard.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당하는 value의 정렬 기준이 없습니다."));
    }

    public boolean isFast() {
        return this.value.equals(FAST.value);
    }

    public boolean isLong() {
        return this.value.equals(LONG.value);
    }
}
