package com.dnd.modutime.core.common;

import java.util.Objects;

public record ErrorResponse(

        String code,
        String message,
        int status
) {
    public ErrorResponse {
        Objects.requireNonNull(code);
    }

    public static ErrorResponse of(String message, int status) {
        return new ErrorResponse("MT" + status, message, status);
    }
}

