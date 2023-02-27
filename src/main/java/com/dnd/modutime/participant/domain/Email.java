package com.dnd.modutime.participant.domain;

import java.util.regex.Pattern;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
public class Email {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$");

    @Column(name = "email")
    private String value;

    public Email(String value) {
        validateRightEmailPattern(value);
        this.value = value;
    }

    private void validateRightEmailPattern(String email) {
        if (!EMAIL_PATTERN.matcher(email).find()) {
            throw new IllegalArgumentException("email 형식에 맞지 않습니다.");
        }
    }

    public String getValue() {
        return value;
    }
}
