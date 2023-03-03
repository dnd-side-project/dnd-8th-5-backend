package com.dnd.modutime.core.adjustresult.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CandidateDateTimeParticipantName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public CandidateDateTimeParticipantName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
