package com.dnd.modutime.core.participant.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.dnd.modutime.core.participant.domain.Email;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class EmailTest {

    @ParameterizedTest
    @ValueSource(strings = {"", "aa", "asd.com", "asd@", "asd@@email.com"})
    void 이메일형식이_맞지않으면_예외를_발생한다(String email) {
        assertThatThrownBy(() -> new Email(email))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
