package com.dnd.modutime.dto.response;

import com.dnd.modutime.participant.domain.Email;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailResponse {

    private String email;

    public static EmailResponse from(Email email) {
        if (email == null) {
            return new EmailResponse(null);
        }
        return new EmailResponse(email.getValue());
    }
}
