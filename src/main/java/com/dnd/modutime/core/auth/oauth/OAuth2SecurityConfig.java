package com.dnd.modutime.core.auth.oauth;

import com.dnd.modutime.core.auth.oauth.facade.OAuth2LogoutService;
import com.dnd.modutime.core.auth.oauth.facade.OAuth2TokenProvider;
import com.dnd.modutime.core.common.ModutimeHostConfigurationProperties;
import com.dnd.modutime.core.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * 아래 @Profile 을 적용하는 방법말고 다른 방법이 통하지 않아 @Profile 을 사용하였습니다.
 * 사용한 방법 1) @MockBean 사용
 * 사용한 방법 2) Test용 Configuration 클래스 생성
 */
@Profile("!test")
@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties({ModutimeHostConfigurationProperties.class})
public class OAuth2SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity httpSecurity,
            AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository,
            AuthenticationSuccessHandler authenticationSuccessHandler,
            AuthenticationFailureHandler authenticationFailureHandler,
            org.springframework.security.oauth2.client.userinfo.OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService,
            OAuth2TokenAuthenticationFilter oAuth2TokenAuthenticationFilter,
            AuthenticationEntryPoint oAuth2AuthenticationEntryPoint,
            AccessDeniedHandler oAuth2AccessDeniedHandler,
            OAuth2AuthorizationRequestResolver authorizationRequestResolver,
            OAuth2LogoutFilter oAuth2LogoutFilter,
            LogoutSuccessHandler logoutSuccessHandler,
            CorsConfigurationSource corsConfigurationSource
    ) throws Exception {
        httpSecurity
                .authorizeHttpRequests(
                        authorizeHttpRequests ->
                                authorizeHttpRequests
                                        .requestMatchers(permitAllMatchers())
                                        .permitAll()
                                        .anyRequest().authenticated()
                )
                .csrf(CsrfConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .oauth2Login(oAuth2LoginConfigurer -> oAuth2LoginConfigurer
                        .authorizationEndpoint(authorizationEndpointConfig -> authorizationEndpointConfig
                                .authorizationRequestResolver(authorizationRequestResolver) // oauth2/authorization/{registrationId}
                                .authorizationRequestRepository(authorizationRequestRepository))
                        .redirectionEndpoint(redirectionEndpointConfig ->
                                redirectionEndpointConfig.baseUri("/oauth2/*/callback")) // 로그인 후 리다이렉트 url 지정
                        .userInfoEndpoint(userInfoEndpointConfig ->
                                userInfoEndpointConfig.userService(oAuth2UserService))
                        .successHandler(authenticationSuccessHandler)
                        .failureHandler(authenticationFailureHandler)
                )
                .logout(logoutConfigurer -> logoutConfigurer
                        .logoutUrl("/oauth2/logout")
                        .logoutSuccessHandler(logoutSuccessHandler)
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .addFilterBefore(oAuth2LogoutFilter, LogoutFilter.class)
                .addFilterBefore(oAuth2TokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> // 필터는 스프링에 대한 예외처리가 되지 않으므로 커스텀 필터 정의
                        exceptionHandling
                                .authenticationEntryPoint(oAuth2AuthenticationEntryPoint)
                                .accessDeniedHandler(oAuth2AccessDeniedHandler)
                );
        return httpSecurity.build();
    }

    /**
     * OAuth 인증이 필요하지 않은 엔드포인트를 정의합니다.
     *
     * @return
     */
    @Bean
    public RequestMatcher permitAllMatchers() {
        return new OrRequestMatcher(
                new AntPathRequestMatcher("/oauth2/kakao/callback"),
                new AntPathRequestMatcher("/oauth2/authorization/**"),
                new AntPathRequestMatcher("/oauth2/reissue-token"),
                new AntPathRequestMatcher("/api/room"),
                new AntPathRequestMatcher("/api/room/**", HttpMethod.GET.toString()),
                new AntPathRequestMatcher("/api/room/*/login", HttpMethod.POST.toString())
        );
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(
                        new AntPathRequestMatcher("/error"),
                        new AntPathRequestMatcher("/favicon.ico")
                );
    }

    /**
     * OAuth2 로그인 과정에서 사용자 정보를 로드하고 처리하는 커스텀 {@link org.springframework.security.oauth2.client.userinfo.OAuth2UserService}를 제공합니다.
     *
     * <p>이 서비스는 OAuth2 제공자(예: 카카오, 구글)로부터 가져온 사용자 정보를 데이터베이스에 저장하거나,
     * 이미 존재하는 사용자를 로드하는 역할을 합니다.</p>
     *
     * @param userRepository 사용자 정보를 관리하는 리포지토리
     * @return 사용자 정보를 로드하고 애플리케이션의 인증된 사용자 객체로 반환하는 {@link org.springframework.security.oauth2.client.userinfo.OAuth2UserService}
     */
    @Bean
    public org.springframework.security.oauth2.client.userinfo.OAuth2UserService<OAuth2UserRequest, OAuth2User>
    oAuth2UserService(UserRepository userRepository, UserCache userCache, ObjectMapper objectMapper) {
        return new OAuth2UserService(userRepository, userCache, objectMapper);
    }


    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
        return new HttpSessionOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler();
    }

    /**
     * 인증되지 않은 사용자가 인증이 필요한 엔드포인트에 접근하려고 할 때의 응답을 커스텀합니다.
     * 기본 동작은 Http Status 401 (Unauthorized)와 함께 스프링 기본 오류 페이지를 반환합니다. 커스텀 EntryPoint를 사용하여 이 동작을 원하는 방식으로 변경할 수 있습니다.
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(ObjectMapper objectMapper) {
        return new OAuth2AuthenticationEntryPoint(objectMapper);
    }

    /**
     * 인증된 사용자가 해당 엔드포인트에 접근할 권한이 없을 때 응답을 커스텀합니다.
     * 기본 동작은 Http Status 403 (Forbidden)을 반환하며, 커스텀 AccessDeniedHandler를 통해 이 동작을 원하는 방식으로 변경할 수 있습니다.
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler(ObjectMapper objectMapper) {
        return new OAuth2AccessDeniedHandler(objectMapper);
    }

    @Bean
    public OAuth2TokenAuthenticationFilter tokenAuthenticationFilter(OAuth2TokenProvider oAuth2TokenProvider,
                                                                     RequestMatcher permitAllMatchers,
                                                                     AuthenticationEntryPoint authenticationEntryPoint) {
        return new OAuth2TokenAuthenticationFilter(oAuth2TokenProvider, permitAllMatchers, authenticationEntryPoint);
    }

    @Bean
    public OAuth2LogoutFilter logoutFilter(ObjectMapper objectMapper, OAuth2LogoutService oAuth2LogoutService) {
        return new OAuth2LogoutFilter(objectMapper, oAuth2LogoutService);
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new OAuth2LogoutSuccessHandler();
    }

    @Bean
    public UserCache userCache() {
        return new OAuth2UserCache();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(ModutimeHostConfigurationProperties hostProperties, Environment environment) {
        var configuration = new CorsConfiguration();

        // TODO: 리팩터링 대상
        if (List.of(environment.getActiveProfiles()).contains("prod")) {
            configuration.setAllowedOriginPatterns(List.of(hostProperties.host().client()));
        } else {
            configuration.setAllowedOriginPatterns(List.of("*"));
        }
        configuration.setAllowedMethods(
                List.of(HttpMethod.GET.toString(),
                        HttpMethod.POST.toString(),
                        HttpMethod.PUT.toString(),
                        HttpMethod.OPTIONS.toString(),
                        HttpMethod.DELETE.toString(),
                        HttpMethod.HEAD.toString()
                ));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
