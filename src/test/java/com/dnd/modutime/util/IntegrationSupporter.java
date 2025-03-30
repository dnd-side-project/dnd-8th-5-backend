package com.dnd.modutime.util;

import com.dnd.modutime.annotations.SpringBootTestWithoutOAuthConfig;
import com.dnd.modutime.core.auth.oauth.OAuth2AuthorizationRequestResolverConfig;
import com.dnd.modutime.core.auth.oauth.facade.OAuth2TokenProvider;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;

@SpringBootTestWithoutOAuthConfig
public abstract class IntegrationSupporter {

    @MockBean
    private OAuth2AuthorizationRequestResolverConfig oAuth2AuthorizationRequestResolverConfig;

    @MockBean
    private OAuth2TokenProvider oAuth2TokenProvider;

    @MockBean
    private OAuth2AuthorizationRequestResolver oAuth2AuthorizationRequestResolver;
}
