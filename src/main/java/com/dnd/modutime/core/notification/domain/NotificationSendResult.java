package com.dnd.modutime.core.notification.domain;

import java.util.Collections;
import java.util.List;

public class NotificationSendResult {

    private final int successCount;
    private final int failureCount;
    private final List<String> failedTokens;

    private NotificationSendResult(int successCount, int failureCount, List<String> failedTokens) {
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.failedTokens = List.copyOf(failedTokens);
    }

    public static NotificationSendResult success(int count) {
        return new NotificationSendResult(count, 0, Collections.emptyList());
    }

    public static NotificationSendResult failure(int successCount, int failureCount, List<String> failedTokens) {
        return new NotificationSendResult(successCount, failureCount, failedTokens);
    }

    public static NotificationSendResult empty() {
        return new NotificationSendResult(0, 0, Collections.emptyList());
    }

    public boolean hasSuccess() {
        return successCount > 0;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public List<String> getFailedTokens() {
        return failedTokens;
    }
}
