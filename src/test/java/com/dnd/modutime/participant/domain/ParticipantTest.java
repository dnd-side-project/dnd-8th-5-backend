package com.dnd.modutime.participant.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ParticipantTest {

    @Test
    void 이름값이_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> getParticipant(null, "1234"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "12345", "abcd"})
    void 비밀번호는_4자리_숫자가_아니라면_예외를_반환한다(String password) {
        assertThatThrownBy(() -> getParticipant("김동호", password))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 이메일을_추가한다() {
        Participant participant = getParticipant("김동호", "1234");
        participant.registerEmail("participant@email.com");
        assertThat(participant.hasEmail()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "aa", "asd.com", "asd@", "asd@@email.com"})
    void 이메일형식이_맞지않으면_예외를_발생한다(String email) {
        Participant participant = getParticipant("김동호", "1234");
        assertThatThrownBy(() -> participant.registerEmail(email))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 이메일은_null이면_이메일을_가지고_있지_않다고_판단한다() {
        Participant participant = getParticipant("김동호", "1234");
        assertThat(participant.hasEmail()).isFalse();
    }

    @Test
    void roomUuid값이_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> getParticipant(null, "participant","1234"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void password가_일치하면_true를_반환한다() {
        Participant participant = getParticipant("김동호", "1234");
        assertThat(participant.matchPassword("1234")).isTrue();
    }

    @Test
    void password가_일치하지_않으면_false를_반환한다() {
        Participant participant = getParticipant("김동호", "1234");
        assertThat(participant.matchPassword("9999")).isFalse();
    }

    private Participant getParticipant(String name, String password) {
        return getParticipant("7c64aa0e-6e8f-4f61-b8ee-d5a86493d3a9", name, password);
    }

    private Participant getParticipant(String roomUuid, String name, String password) {
        return new Participant(roomUuid, name, password);
    }
}
