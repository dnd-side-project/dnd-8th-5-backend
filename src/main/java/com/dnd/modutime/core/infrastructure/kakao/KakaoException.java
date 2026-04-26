package com.dnd.modutime.core.infrastructure.kakao;

import com.dnd.modutime.core.infrastructure.common.ClientException;

public class KakaoException extends ClientException {

    public KakaoException(final String message) {
        super(message);
    }

    public KakaoException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public static class KakaoClientException extends KakaoException {
        public KakaoClientException(final String message) {
            super(message);
        }
    }

    public static class KakaoServerException extends KakaoException {
        public KakaoServerException(final String message) {
            super(message);
        }
    }
}
