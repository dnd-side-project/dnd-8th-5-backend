package com.dnd.modutime.timeblock.application;

import com.dnd.modutime.timeblock.domain.TimeBlock;
import com.dnd.modutime.timeblock.repository.TimeBlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class TimeBlockCreateService {

    private final TimeBlockRepository timeBlockRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void create(ParticipantCreationEvent event) {
        timeBlockRepository.save(new TimeBlock(event.getRoomUuid(), event.getName()));
    }
}
