package com.dnd.modutime.core.timetable.domain;

import com.dnd.modutime.core.entity.Auditable;
import com.dnd.modutime.core.timeblock.domain.AvailableDateTime;
import com.dnd.modutime.core.timeblock.domain.AvailableTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DateInfo implements Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_table_id")
    private TimeTable timeTable;

    @Column(nullable = false)
    private LocalDate date;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    @JoinColumn(
            name = "date_info_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_time_info_date_info_id_ref_date_info_id")
    )
    private List<TimeInfo> timeInfos;

    private String createdBy;
    private LocalDateTime createdAt;
    private String modifiedBy;
    private LocalDateTime modifiedAt;

    public DateInfo(TimeTable timeTable,
                    LocalDate date,
                    List<TimeInfo> timeInfos) {
        this.timeTable = timeTable;
        this.date = date;
        this.timeInfos = timeInfos;
    }

    public List<Long> getTimeInfoIdsByAvailableDateTime(AvailableDateTime availableDateTime) {
        final LocalDate date = availableDateTime.getDate();
        final List<AvailableTime> timesOrNull = availableDateTime.getTimesOrNull();
        if (!this.date.isEqual(date)) {
            return List.of();
        }
        if (timesOrNull == null || timesOrNull.isEmpty()) {
            return List.of(timeInfos.get(0).getId());
        }
        List<Long> timeInfoIds = new ArrayList<>();
        for (TimeInfo timeInfo : timeInfos) {
            for (AvailableTime availableTime : timesOrNull) {
                if (timeInfo.isSameTime(availableTime.getTime())) {
                    timeInfoIds.add(timeInfo.getId());
                }
            }
        }
        return timeInfoIds;
    }

    public void removeParticipantNameIfSameDate(AvailableDateTime availableDateTime, String participantName) {
        if (!date.isEqual(availableDateTime.getDate())) {
            return;
        }
        List<AvailableTime> timesOrNull = availableDateTime.getTimesOrNull();
        if (timesOrNull == null || timesOrNull.isEmpty()) {
            validateTimeInfoIsEmpty();
            TimeInfo timeInfo = timeInfos.get(0);
            timeInfo.removeParticipantName(participantName);
            return;
        }
        timeInfos.forEach(
                timeInfo -> timesOrNull.forEach(
                        availableTime -> timeInfo.removeParticipantNameIfSameTime(availableTime.getTime(), participantName)
                )
        );
    }

    public void removeParticipantNameByTimeInfoId(List<Long> timeInfoIds, String participantName) {
        timeInfos.forEach(
                timeInfo -> timeInfo.removeParticipantByTimeInfoIds(timeInfoIds, participantName)
        );
    }

    public void addParticipantNameByTimeInfoId(List<Long> timeInfoIds, String participantName) {
        timeInfos.forEach(
                timeInfo -> timeInfo.addParticipantByTimeInfoIds(timeInfoIds, participantName)
        );
    }

    public void addParticipantNameIfSameDate(AvailableDateTime availableDateTime,
                                             String participantName) {
        if (!date.isEqual(availableDateTime.getDate())) {
            return;
        }
        List<AvailableTime> timesOrNull = availableDateTime.getTimesOrNull();
        if (timesOrNull == null) {
            validateTimeInfoIsEmpty();
            TimeInfo timeInfo = timeInfos.get(0);
            timeInfo.addParticipantName(participantName);
            return;
        }
        timeInfos.forEach(
                timeInfo -> timesOrNull.forEach(
                        availableTime -> timeInfo.addParticipantNameIfSameTime(availableTime.getTime(), participantName))
        );
    }

    private void validateTimeInfoIsEmpty() {
        if (timeInfos.isEmpty()) {
            throw new IllegalArgumentException("timeInfo가 비어있을 수 없습니다.");
        }
    }

    public List<TimeInfo> getTimeInfosByParticipantNames(List<String> participantNames) {
        return timeInfos.stream()
                .filter(timeInfo -> timeInfo.containsAllParticipantName(participantNames))
                .collect(Collectors.toList());
    }

    public List<TimeInfo> hasAnyParticipant(List<String> participantNames) {
        return timeInfos.stream()
                .filter(timeInfo -> timeInfo.hasAnyParticipant(participantNames))
                .collect(Collectors.toList());
    }

    public LocalDate getDate() {
        return date;
    }

    public List<TimeInfo> getTimeInfos() {
        return timeInfos;
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
