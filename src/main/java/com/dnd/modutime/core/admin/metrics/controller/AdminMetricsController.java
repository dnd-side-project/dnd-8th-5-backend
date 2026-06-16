package com.dnd.modutime.core.admin.metrics.controller;

import com.dnd.modutime.core.admin.metrics.application.AdminMetricsService;
import com.dnd.modutime.core.admin.metrics.application.response.ServiceMetrics;
import com.dnd.modutime.core.admin.metrics.application.response.ServiceTrends;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 어드민 "대시보드" 메뉴용 메트릭 API.
 *
 * <p>{@code /admin/**} 경로는 {@code AdminSecurityConfig} 체인이 어드민 access token 인증을 강제하므로
 * 별도 인증 애너테이션은 필요 없다.</p>
 */
@RestController
@RequiredArgsConstructor
public class AdminMetricsController {

    private final AdminMetricsService adminMetricsService;

    @GetMapping("/admin/api/v1/metrics")
    public ServiceMetrics getMetrics() {
        return adminMetricsService.getMetrics();
    }

    @GetMapping("/admin/api/v1/metrics/trends")
    public ServiceTrends getTrends() {
        return adminMetricsService.getTrends();
    }
}
