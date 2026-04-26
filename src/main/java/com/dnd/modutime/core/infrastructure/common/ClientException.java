package com.dnd.modutime.core.infrastructure.common;

public class ClientException extends RuntimeException {

    public ClientException(final String message) {
        super(message);
    }

    public ClientException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
