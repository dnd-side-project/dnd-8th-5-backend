package com.dnd.modutime.timetable.domain;

import java.time.LocalTime;
import java.util.List;
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
    private List<TimeTableParticipantName> timeTableParticipantNames;

    public TimeInfo(LocalTime time,
                    List<TimeTableParticipantName> timeTableParticipantNames) {
        this.time = time;
        this.timeTableParticipantNames = timeTableParticipantNames;
    }

    public void removeParticipantNameIfSameTime(LocalTime time, String participantName) {
        if (this.time.equals(time)) {
            removeParticipantName(participantName);
        }
    }

    public void removeParticipantName(String participantName) {
        timeTableParticipantNames.removeIf(
                timeTableParticipantName -> timeTableParticipantName.isSameName(participantName));
    }

    public void addParticipantNameIfSameTime(LocalTime time,
                                             String participantName) {
        if (this.time.equals(time)) {
            addParticipantName(participantName);
        }
    }

    public void addParticipantName(String participantName) {
        if (!containsParticipantName(participantName)) {
            timeTableParticipantNames.add(new TimeTableParticipantName(this, participantName));
        }
    }

    private boolean containsParticipantName(String participantName) {
        return timeTableParticipantNames.stream()
                .anyMatch(timeTableParticipantName -> timeTableParticipantName.isSameName(participantName));
    }

    public int getParticipantsSize() {
        return timeTableParticipantNames.size();
    }

    public LocalTime getTime() {
        return time;
    }

    public List<TimeTableParticipantName> getTimeTableParticipantNames() {
        return timeTableParticipantNames;
    }
}
