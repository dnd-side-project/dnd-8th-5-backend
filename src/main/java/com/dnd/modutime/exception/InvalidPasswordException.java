package com.dnd.modutime.exception;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super("비밀번호가 일치하지 않습니다.");
    }
}
