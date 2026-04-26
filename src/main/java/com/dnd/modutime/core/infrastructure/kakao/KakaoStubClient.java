package com.dnd.modutime.core.infrastructure.kakao;

import com.dnd.modutime.core.infrastructure.kakao.config.dto.KakaoUnlinkResponse;

public class KakaoStubClient implements KakaoClient {

    @Override
    public KakaoUnlinkResponse unlink(final String targetId, final String targetIdType) {
        return new KakaoUnlinkResponse(parseTargetId(targetId));
    }

    private Long parseTargetId(final String targetId) {
        try {
            return Long.parseLong(targetId);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
