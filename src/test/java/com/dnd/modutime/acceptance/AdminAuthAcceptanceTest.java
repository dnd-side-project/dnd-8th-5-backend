package com.dnd.modutime.acceptance;

import com.dnd.modutime.core.admin.application.request.AdminLoginRequest;
import com.dnd.modutime.core.admin.application.response.AdminLoginResponse;
import com.dnd.modutime.core.admin.application.response.AdminReissueTokenResponse;
import com.dnd.modutime.core.admin.controller.dto.AdminReissueTokenRequest;
import com.dnd.modutime.core.admin.domain.Admin;
import com.dnd.modutime.core.admin.repository.AdminRepository;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * 어드민 인증 흐름 acceptance.
 *
 * <p>{@code test} 프로파일에서는 {@code AdminSecurityConfig}(@Profile("!test")) 가 비활성화되어
 * 필터 없이 컨트롤러/서비스 로직만 검증한다. 토큰 발급은 실제 {@code AdminTokenProvider} 가 수행한다.
 * 필터 단의 토큰 격리/401 동작은 e2e(실제 부팅) 또는 단위 테스트로 검증한다.</p>
 */
public class AdminAuthAcceptanceTest extends AcceptanceSupporter {

    private static final String USERNAME = "superadmin";
    private static final String PASSWORD = "pw1234";

    @Autowired
    private AdminRepository adminRepository;

    @BeforeEach
    void setUpAdmin() {
        adminRepository.deleteAll();
        adminRepository.save(new Admin(USERNAME, PASSWORD));
    }

    @DisplayName("어드민 로그인 - 올바른 자격증명이면 access/refresh 토큰을 발급한다")
    @Test
    void 로그인_성공() {
        ExtractableResponse<Response> response = post("/admin/login", new AdminLoginRequest(USERNAME, PASSWORD));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        AdminLoginResponse body = response.body().as(AdminLoginResponse.class);
        assertAll(
                () -> assertThat(body.accessToken()).isNotBlank(),
                () -> assertThat(body.accessTokenExpirationTime()).isNotNull(),
                () -> assertThat(body.refreshToken()).isNotBlank(),
                () -> assertThat(body.refreshTokenExpirationTime()).isNotNull()
        );
    }

    @DisplayName("어드민 로그인 - 비밀번호가 틀리면 401")
    @Test
    void 로그인_비밀번호_불일치() {
        ExtractableResponse<Response> response = post("/admin/login", new AdminLoginRequest(USERNAME, "wrong"));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @DisplayName("어드민 로그인 - 존재하지 않는 username 이면 401")
    @Test
    void 로그인_존재하지_않는_계정() {
        ExtractableResponse<Response> response = post("/admin/login", new AdminLoginRequest("nobody", PASSWORD));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @DisplayName("어드민 로그인 - username 이 비어있으면 400")
    @Test
    void 로그인_입력_검증() {
        ExtractableResponse<Response> response = post("/admin/login", new AdminLoginRequest("", PASSWORD));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("어드민 토큰 재발급 - 로그인으로 받은 refreshToken 으로 새 accessToken 을 발급한다")
    @Test
    void 재발급_성공() {
        AdminLoginResponse login = post("/admin/login", new AdminLoginRequest(USERNAME, PASSWORD))
                .body().as(AdminLoginResponse.class);

        ExtractableResponse<Response> response = post("/admin/reissue-token",
                new AdminReissueTokenRequest(login.refreshToken()));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        AdminReissueTokenResponse body = response.body().as(AdminReissueTokenResponse.class);
        assertAll(
                () -> assertThat(body.accessToken()).isNotBlank(),
                () -> assertThat(body.accessTokenExpirationTime()).isNotNull()
        );
    }

    @DisplayName("어드민 토큰 재발급 - 유효하지 않은 refreshToken 이면 401")
    @Test
    void 재발급_유효하지_않은_토큰() {
        ExtractableResponse<Response> response = post("/admin/reissue-token",
                new AdminReissueTokenRequest("invalid-refresh-token"));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @DisplayName("어드민 토큰 재발급 - refreshToken 이 없으면 401")
    @Test
    void 재발급_입력_누락() {
        ExtractableResponse<Response> response = post("/admin/reissue-token",
                new AdminReissueTokenRequest(null));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @DisplayName("어드민 토큰 재발급 - 바디 없이 refreshToken 쿠키만으로도 발급된다")
    @Test
    void 재발급_쿠키() {
        AdminLoginResponse login = post("/admin/login", new AdminLoginRequest(USERNAME, PASSWORD))
                .body().as(AdminLoginResponse.class);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .cookie("refreshToken", login.refreshToken())
                .when().post("/admin/reissue-token")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().as(AdminReissueTokenResponse.class).accessToken()).isNotBlank();
    }

    @DisplayName("어드민 토큰 재발급 - 쿠키와 바디가 함께 오면 바디가 우선한다")
    @Test
    void 재발급_바디_우선() {
        AdminLoginResponse login = post("/admin/login", new AdminLoginRequest(USERNAME, PASSWORD))
                .body().as(AdminLoginResponse.class);

        // 쿠키는 유효한 refreshToken, 바디는 유효하지 않은 값 → 바디가 우선 적용되어 401 이어야 한다.
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .cookie("refreshToken", login.refreshToken())
                .body(new AdminReissueTokenRequest("invalid-refresh-token"))
                .when().post("/admin/reissue-token")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
}
