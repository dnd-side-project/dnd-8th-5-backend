package com.dnd.modutime.core.timetable.domain;

import com.dnd.modutime.core.entity.Auditable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeInfo implements Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalTime time;

    @OneToMany(mappedBy = "timeInfo", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    private List<TimeInfoParticipantName> timeInfoParticipantNames;

    private String createdBy;
    private LocalDateTime createdAt;
    private String modifiedBy;
    private LocalDateTime modifiedAt;

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

    public void removeParticipantByTimeInfoIds(List<Long> timeInfoIds, String participantName) {
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

    public void addParticipantByTimeInfoIds(List<Long> timeInfoIds, String participantName) {
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

    public LocalTime getTimeOrZeroTime() {
        if (time == null) {
            return LocalTime.of(0, 0);
        }
        return time;
    }

    public List<TimeInfoParticipantName> getTimeInfoParticipantNames() {
        return timeInfoParticipantNames;
    }

    // TODO: test
    public boolean isSameTime(LocalTime time) {
        return this.time.equals(time);
    }

    // TODO: test
    public boolean hasTime() {
        return time != null;
    }

    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Override
    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}
