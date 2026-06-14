package com.dnd.modutime.core.feedback.domain;

/**
 * 제출 시점의 컨텍스트 스냅샷. 프론트가 구조를 설계했으며 통째로 JSON 블롭({@code snapshot} 컬럼)에 저장된다.
 *
 * <p>{@code user}/{@code room}은 비회원이거나 방 컨텍스트가 없을 때 null일 수 있다.
 * {@code room.deadLine}은 ISO 문자열 또는 null이며, 파싱하지 않고 원문 String으로 보관한다.</p>
 */
public record Snapshot(User user, Room room, Page page, Env env) {

    public record User(String name, String email) {
    }

    public record Room(
            String roomId,
            String type,
            String title,
            Integer participantCount,
            Integer headCount,
            String deadLine
    ) {
    }

    public record Page(String routeId, String pathname, String search) {
    }

    public record Env(String userAgent, Viewport viewport, String appVersion) {
    }

    public record Viewport(Integer width, Integer height) {
    }
}
