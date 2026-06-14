package com.dnd.modutime.core.admin.config;

import com.dnd.modutime.core.admin.domain.Admin;
import com.dnd.modutime.core.admin.repository.AdminRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * 로컬 개발/테스트 편의를 위해 부팅 시 어드민 계정(superadmin / 1234)을 보장한다.
 *
 * <p>local 프로파일에서만 동작하며 운영(prod)에서는 절대 실행되지 않는다. 로컬 H2 는 in-memory 라
 * 재부팅 시 데이터가 초기화되므로, 매 부팅마다 계정이 없으면 생성한다(멱등).</p>
 */
@Slf4j
@Profile("local")
@Component
public class AdminLocalDataInitializer implements ApplicationRunner {

    private static final String DEFAULT_USERNAME = "superadmin";
    private static final String DEFAULT_PASSWORD = "1234";

    private final AdminRepository adminRepository;

    public AdminLocalDataInitializer(final AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public void run(final ApplicationArguments args) {
        if (adminRepository.findByUsername(DEFAULT_USERNAME).isPresent()) {
            return;
        }
        adminRepository.save(new Admin(DEFAULT_USERNAME, DEFAULT_PASSWORD));
        log.info("[local] 테스트용 어드민 계정 생성: username={}, password={}", DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }
}
