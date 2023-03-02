package com.dnd.modutime.timetable.domain;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalTime time;

    @OneToMany(mappedBy = "timeInfo", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    private List<TimeInfoParticipantName> timeInfoParticipantNames;

    public TimeInfo(LocalTime time,
                    List<TimeInfoParticipantName> timeInfoParticipantNames) {
        this.time = time;
        this.timeInfoParticipantNames = timeInfoParticipantNames;
    }

    public void removeParticipantNameIfSameTime(LocalTime time, String participantName) {
        if (this.time.equals(time)) {
            removeParticipantName(participantName);
        }
    }

    public void removeParticipantName(String participantName) {
        timeInfoParticipantNames.removeIf(
                timeTableParticipantName -> timeTableParticipantName.isSameName(participantName));
    }

    public void removeParticipantByTimeInfoIds(final List<Long> timeInfoIds, final String participantName) {
        if (timeInfoIds.contains(this.id)) {
            removeParticipantName(participantName);
        }
    }

    public void addParticipantNameIfSameTime(LocalTime time,
                                             String participantName) {
        if (this.time.equals(time)) {
            addParticipantName(participantName);
        }
    }

    public void addParticipantName(String participantName) {
        if (!containsParticipantName(participantName)) {
            timeInfoParticipantNames.add(new TimeInfoParticipantName(this, participantName));
        }
    }

    public void addParticipantByTimeInfoIds(final List<Long> timeInfoIds, final String participantName) {
        if (timeInfoIds.contains(this.id)) {
            addParticipantName(participantName);
        }
    }

    private boolean containsParticipantName(String participantName) {
        return timeInfoParticipantNames.stream()
                .anyMatch(timeTableParticipantName -> timeTableParticipantName.isSameName(participantName));
    }

    // TODO: test
    public boolean containsAllParticipantName(List<String> participantNames) {
        return timeInfoParticipantNames.stream()
                .map(TimeInfoParticipantName::getName)
                .collect(Collectors.toList())
                .containsAll(participantNames);
    }

    public Long getId() {
        return id;
    }

    public int getParticipantsSize() {
        return timeInfoParticipantNames.size();
    }

    public LocalTime getTime() {
        return time;
    }

    public List<TimeInfoParticipantName> getTimeInfoParticipantNames() {
        return timeInfoParticipantNames;
    }

    // TODO: test
    public boolean isSameTime(final LocalTime time) {
        return this.time.equals(time);
    }
}
