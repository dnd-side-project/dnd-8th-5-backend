package com.dnd.modutime.integration;

import com.dnd.modutime.application.ParticipantService;
import com.dnd.modutime.domain.Participant;
import com.dnd.modutime.dto.request.ParticipantRequest;
import com.dnd.modutime.repository.ParticipantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ParticipantIntegrationTest {
    
    @Autowired
    private ParticipantService participantService;

    @Autowired
    private ParticipantRepository participantRepository;
    
    @Test
    void 참여자를_생성한다() {
        ParticipantRequest request = new ParticipantRequest("participant1", "participant1@email.com");
        participantService.create("7c64aa0e-6e8f-4f61-b8ee-d5a86493d3a9", request);
        Optional<Participant> actual = participantRepository.findByRoomUuidAndName("7c64aa0e-6e8f-4f61-b8ee-d5a86493d3a9", "participant1");
        assertThat(actual.isPresent()).isTrue();
    }
}
