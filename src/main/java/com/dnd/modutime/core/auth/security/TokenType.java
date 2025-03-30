package com.dnd.modutime.core.auth.security;

public enum TokenType {

    ACCESS, REFRESH;

    public static boolean isAccessToken(final String tokenType) {
        return TokenType.ACCESS.name().equals(tokenType);
    }
}
