package com.dnd.modutime.core.infrastructure.kakao;

import com.dnd.modutime.core.infrastructure.kakao.config.dto.KakaoUnlinkResponse;

public interface KakaoClient {

    KakaoUnlinkResponse unlink(String targetId, String targetIdType);

    default KakaoUnlinkResponse unlinkByUserId(final String oauthId) {
        return unlink(oauthId, "user_id");
    }
}
