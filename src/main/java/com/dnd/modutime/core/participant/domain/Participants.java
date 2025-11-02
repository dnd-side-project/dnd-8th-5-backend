package com.dnd.modutime.core.participant.domain;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

// TODO: test
public class Participants {

    private final List<Participant> participants;

    public Participants(List<Participant> participants) {
        validateParticipantsNull(participants);
        this.participants = participants;
    }

    private void validateParticipantsNull(List<Participant> participants) {
        if (participants == null) {
            throw new IllegalArgumentException("참여자에 null일 수 없습니다.");
        }
    }

    public boolean isSameAllNames(List<String> names) {
        validateNamesNull(names);
        List<String> participantNames = getParticipantNames();
        return new HashSet<>(names).containsAll(participantNames);
    }

    public boolean containsAll(List<String> names) {
        if (names == null || names.isEmpty()) {
            return true;
        }
        var participantNames = getParticipantNames();
        return new HashSet<>(participantNames).containsAll(names);
    }

    private List<String> getParticipantNames() {
        return participants.stream()
                .map(Participant::getName)
                .collect(Collectors.toList());
    }

    private void validateNamesNull(List<String> names) {
        if (names == null) {
            throw new IllegalArgumentException("이름에 null이 올 수 없습니다.");
        }
    }

    public List<String> getExcludedParticipantNames(List<String> availableParticipantNames) {
        return participants.stream()
                .map(Participant::getName)
                .filter(name -> !availableParticipantNames.contains(name))
                .collect(Collectors.toList());
    }
}
