package com.dnd.modutime.core.timetable.domain;

import com.dnd.modutime.core.adjustresult.application.DateTimeInfoDto;
import com.dnd.modutime.core.entity.Auditable;
import com.dnd.modutime.core.timeblock.domain.AvailableDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * TimeTable은 '방에 할당된 시간표' 를 나타내는 엔티티입니다.
 * 방에 속한 모든 날짜와 시간 정보를 포함하고 있으며, 각 날짜/시간에 등록한 참여자 정보도 포함합니다.
 * 참여자가 자신의 TimeBlock 을 수정하면 TimeTable도 수정됩니다.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeTable extends AbstractAggregateRoot<TimeTable> implements Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String roomUuid;

    @OneToMany(mappedBy = "timeTable", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    private List<DateInfo> dateInfos = List.of();

    private String createdBy;
    private LocalDateTime createdAt;
    private String modifiedBy;
    private LocalDateTime modifiedAt;

    public TimeTable(String roomUuid) {
        this.roomUuid = roomUuid;
    }

    public void replaceDateInfos(List<DateInfo> dateInfos) {
        this.dateInfos = dateInfos;
    }

    // TODO: id 사용하는걸로 교체
    public void updateParticipantName(List<AvailableDateTime> oldAvailableDateTimes,
                                      List<AvailableDateTime> newAvailableDateTimes,
                                      String participantName) {
        removeParticipantName(oldAvailableDateTimes, participantName);
        addParticipantName(newAvailableDateTimes, participantName);
        registerEvent(new TimeTableReplaceEvent(roomUuid, dateInfos)); // TODO
    }

    public List<Long> getTimeInfoIdsByAvailableDateTimes(List<AvailableDateTime> availableDateTimes) {
        List<Long> timeInfoIds = new ArrayList<>();
        for (DateInfo dateInfo : dateInfos) {
            for (AvailableDateTime availableDateTime : availableDateTimes) {
                timeInfoIds.addAll(dateInfo.getTimeInfoIdsByAvailableDateTime(availableDateTime));
            }
        }
        return timeInfoIds;
    }

    private void removeParticipantNameByTimeInfoIds(List<Long> timeInfoIds, String participantName) {
        dateInfos.forEach(
                dateInfo -> dateInfo.removeParticipantNameByTimeInfoId(timeInfoIds, participantName)
        );
    }

    private void addParticipantNameByTimeInfoIds(List<Long> timeInfoIds, String participantName) {
        dateInfos.forEach(
                dateInfo -> dateInfo.addParticipantNameByTimeInfoId(timeInfoIds, participantName)
        );
    }

    public void removeParticipantName(List<AvailableDateTime> availableDateTimes, String participantName) {
        availableDateTimes.forEach(
                availableDateTime -> dateInfos.forEach(
                        dateInfo -> dateInfo.removeParticipantNameIfSameDate(availableDateTime, participantName)
                )
        );
    }

    public void addParticipantName(List<AvailableDateTime> availableDateTimes, String participantName) {
        availableDateTimes.forEach(
                availableDateTime -> dateInfos.forEach(
                        dateInfo -> dateInfo.addParticipantNameIfSameDate(availableDateTime, participantName)
                )
        );
    }

    public List<DateTimeInfoDto> getDateTimeInfosDtoByParticipantNames(List<String> participantNames) {
        List<DateTimeInfoDto> dateTimeInfosDto = new ArrayList<>();
        for (DateInfo dateInfo : dateInfos) {
            List<TimeInfo> timeInfos = dateInfo.getTimeInfosByParticipantNames(participantNames);
            timeInfos.forEach(
                    timeInfo -> dateTimeInfosDto.add(new DateTimeInfoDto(LocalDateTime.of(dateInfo.getDate(), timeInfo.getTimeOrZeroTime()), participantNames))
            );
        }
        return dateTimeInfosDto;
    }

    public List<DateInfo> getDateInfos() {
        return dateInfos;
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
