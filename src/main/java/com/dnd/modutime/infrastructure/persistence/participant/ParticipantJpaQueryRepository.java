package com.dnd.modutime.infrastructure.persistence.participant;

import com.dnd.modutime.core.participant.domain.Participant;
import com.dnd.modutime.core.participant.domain.ParticipantQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantJpaQueryRepository extends JpaRepository<Participant, Long>,
        ParticipantQueryRepository {
}
