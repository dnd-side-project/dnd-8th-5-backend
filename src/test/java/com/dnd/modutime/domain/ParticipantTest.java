package com.dnd.modutime.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class ParticipantTest {

    @Test
    void 이름값이_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> getParticipant(null, "participant@email.com"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 이메일은_null이면_이메일을_가지고_있지_않다고_판단한다() {
        Participant participant = getParticipant("participant", null);
        assertThat(participant.hasEmail()).isFalse();
    }

    @Test
    void 이메일은_null이_아니면_이메일을_가지고_있다고_판단한다() {
        Participant participant = getParticipant("participant", "participant@email.com");
        assertThat(participant.hasEmail()).isTrue();
    }

    @Test
    void roomUuid값이_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> getParticipant(null, "participant","participant@email.com"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private Participant getParticipant(String name, String email) {
        return getParticipant("7c64aa0e-6e8f-4f61-b8ee-d5a86493d3a9", name, email);
    }

    private Participant getParticipant(String roomUuid, String name, String email) {
        return new Participant(roomUuid, name, email);
    }
}
