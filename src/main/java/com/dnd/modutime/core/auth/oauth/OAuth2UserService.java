package com.dnd.modutime.core.auth.oauth;

import com.dnd.modutime.core.user.User;
import com.dnd.modutime.core.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserCache userCache;
    private final ObjectMapper objectMapper;

    public OAuth2UserService(final UserRepository userRepository, final UserCache userCache, final ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.userCache = userCache;
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

        OAuth2UserDetails oAuth2UserDetails = OAuth2UserDetails.of(registrationId, attributes, objectMapper);

        String cachedKey = registrationId + ":" + oAuth2UserDetails.email();
        UserDetails cachedUser = this.userCache.getUserFromCache(cachedKey);
        if (cachedUser != null) {
            return (OAuth2User) cachedUser;
        }

        User user = getOrSaveUser(oAuth2UserDetails);

        OAuth2User OAuth2User = new OAuth2User(user, attributes, userNameAttributeName);
        this.userCache.putUserInCache(OAuth2User);
        log.debug("User {} is cached", OAuth2User.getUsername());

        return OAuth2User;
    }

    private User getOrSaveUser(final OAuth2UserDetails oAuth2UserDetails) {
        User user = this.userRepository.findByEmailAndProvider(oAuth2UserDetails.email(), oAuth2UserDetails.oAuth2Provider())
                .orElseGet(oAuth2UserDetails::toEntity);

        return this.userRepository.save(user);
    }
}
