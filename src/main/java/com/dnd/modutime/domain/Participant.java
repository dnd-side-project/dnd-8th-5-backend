package com.dnd.modutime.domain;

public class Participant {

    private final String roomUuid;
    private final String name;
    private final String email;

    public Participant(String roomUuid, String name, String email) {
        validateRoomUuid(roomUuid);
        validateName(name);
        this.roomUuid = roomUuid;
        this.name = name;
        this.email = email;
    }

    private void validateRoomUuid(String roomUuid) {
        if (roomUuid == null) {
            throw new IllegalArgumentException("roomUuid는 null일 수 없습니다");
        }
    }

    private static void validateName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("이름은 null일 수 없습니다");
        }
    }

    public boolean hasEmail() {
        return email != null;
    }

    public String getRoomUuid() {
        return roomUuid;
    }

    public String getName() {
        return name;
    }
}
