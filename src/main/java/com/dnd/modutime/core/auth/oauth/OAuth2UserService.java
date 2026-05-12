package com.dnd.modutime.core.auth.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final OAuth2UserResolver oAuth2UserResolver;
    private final ObjectMapper objectMapper;

    public OAuth2UserService(final OAuth2UserResolver oAuth2UserResolver, final ObjectMapper objectMapper) {
        this.oAuth2UserResolver = oAuth2UserResolver;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public org.springframework.security.oauth2.core.user.OAuth2User loadUser(final OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        Map<String, Object> attributes = super.loadUser(userRequest).getAttributes();

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        OAuth2UserDetails details = OAuth2UserDetails.of(registrationId, attributes, objectMapper);

        return this.oAuth2UserResolver.resolveAndCache(details, attributes, userNameAttributeName);
    }
}
