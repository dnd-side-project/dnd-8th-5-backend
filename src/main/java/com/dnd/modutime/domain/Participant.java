package com.dnd.modutime.domain;

import java.util.regex.Pattern;

public class Participant {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[0-9]{4}$");

    private final String roomUuid;
    private final String name;
    private final String password;
    private String email;

    public Participant(String roomUuid, String name, String password) {
        validateRoomUuid(roomUuid);
        validateName(name);
        validatePassword(password);

        this.roomUuid = roomUuid;
        this.name = name;
        this.password = password;
        this.email = null;
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

    private void validatePassword(String password) {
        if (isRightPassword(password)) {
            throw new IllegalArgumentException("비밀번호는 4자리 숫자여야 합니다.");
        }
    }

    private boolean isRightPassword(String password) {
        return !PASSWORD_PATTERN.matcher(password).find();
    }

    public void registerEmail(String email) {
        this.email = email;
    }

    public boolean hasEmail() {
        return email != null;
    }

    public boolean matchPassword(String password) {
        return this.password.equals(password);
    }

    public String getRoomUuid() {
        return roomUuid;
    }

    public String getName() {
        return name;
    }
}
