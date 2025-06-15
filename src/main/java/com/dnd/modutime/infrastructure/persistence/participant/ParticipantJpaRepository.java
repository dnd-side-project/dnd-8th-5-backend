package com.dnd.modutime.infrastructure.persistence.participant;

import com.dnd.modutime.core.participant.domain.Participant;
import com.dnd.modutime.core.participant.domain.ParticipantRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantJpaRepository extends JpaRepository<Participant, Long>,
        ParticipantRepository {
}
