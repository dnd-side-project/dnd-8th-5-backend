package com.dnd.modutime.core.admin.security;

import com.dnd.modutime.core.admin.application.AdminTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * 어드민 전용 SecurityFilterChain.
 *
 * <p>{@code /admin/**} 만 담당하는 별도 체인으로, 기존 사용자(OAuth2) 인증 체인과 완전히 분리된다.
 * {@link Order @Order(1)} 로 기존 전역 체인({@code @Order(2)})보다 먼저 평가된다.
 * 로그인/재발급은 permitAll, 그 외 어드민 API 는 어드민 access token 인증을 요구한다.</p>
 */
@Profile("!test")
@Configuration
public class AdminSecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain adminFilterChain(
            HttpSecurity httpSecurity,
            AdminTokenAuthenticationFilter adminTokenAuthenticationFilter,
            AdminAuthenticationEntryPoint adminAuthenticationEntryPoint,
            CorsConfigurationSource corsConfigurationSource
    ) throws Exception {
        httpSecurity
                .antMatcher("/admin/**")
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
                                .requestMatchers(adminPermitAllMatchers())
                                .permitAll()
                                .anyRequest().authenticated()
                )
                .csrf(CsrfConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(adminTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(adminAuthenticationEntryPoint));
        return httpSecurity.build();
    }

    /**
     * 어드민 인증이 필요하지 않은 엔드포인트(로그인/토큰 재발급)를 정의한다.
     */
    @Bean
    public RequestMatcher adminPermitAllMatchers() {
        return new OrRequestMatcher(
                new AntPathRequestMatcher("/admin/login"),
                new AntPathRequestMatcher("/admin/reissue-token")
        );
    }

    @Bean
    public AdminTokenAuthenticationFilter adminTokenAuthenticationFilter(
            AdminTokenProvider adminTokenProvider,
            RequestMatcher adminPermitAllMatchers,
            AdminAuthenticationEntryPoint adminAuthenticationEntryPoint) {
        return new AdminTokenAuthenticationFilter(adminTokenProvider, adminPermitAllMatchers, adminAuthenticationEntryPoint);
    }

    /**
     * {@link AdminTokenAuthenticationFilter} 가 @Bean 으로 등록되면 Spring Boot 가 이를 모든 요청에
     * 적용되는 전역 서블릿 필터로 자동 등록한다. 어드민 필터는 {@code /admin/**} 보안 체인 안에서만
     * 동작해야 하므로 전역 자동 등록을 비활성화한다. (체인 참여는 addFilterBefore 로 유지된다.)
     */
    @Bean
    public FilterRegistrationBean<AdminTokenAuthenticationFilter> adminTokenAuthenticationFilterRegistration(
            AdminTokenAuthenticationFilter adminTokenAuthenticationFilter) {
        var registration = new FilterRegistrationBean<>(adminTokenAuthenticationFilter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public AdminAuthenticationEntryPoint adminAuthenticationEntryPoint(ObjectMapper objectMapper) {
        return new AdminAuthenticationEntryPoint(objectMapper);
    }
}
