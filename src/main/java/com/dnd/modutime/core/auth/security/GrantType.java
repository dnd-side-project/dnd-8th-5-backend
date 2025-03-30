package com.dnd.modutime.core.auth.security;

import lombok.Getter;

@Getter
public enum GrantType {

    BEARER("Bearer");

    GrantType(final String type) {
        this.type = type;
    }

    private String type;
}
