package com.dnd.modutime.core.auth.oauth;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.concurrent.TimeUnit;

public class OAuth2UserCache implements UserCache {

    private final Cache<String, UserDetails> cache;

    public OAuth2UserCache() {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(500)
                .build();
    }

    @Override
    public UserDetails getUserFromCache(final String username) {
        return this.cache.getIfPresent(username);
    }

    @Override
    public void putUserInCache(final UserDetails user) {
        this.cache.put(user.getUsername(), user);
    }

    @Override
    public void removeUserFromCache(final String username) {
        this.cache.invalidate(username);
    }

}
