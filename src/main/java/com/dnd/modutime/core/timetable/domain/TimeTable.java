package com.dnd.modutime.core.timetable.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.springframework.data.domain.AbstractAggregateRoot;

import com.dnd.modutime.core.adjustresult.application.DateTimeInfoDto;
import com.dnd.modutime.core.timeblock.domain.AvailableDateTime;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeTable extends AbstractAggregateRoot<TimeTable> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String roomUuid;

    @OneToMany(mappedBy = "timeTable", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    private List<DateInfo> dateInfos = List.of();

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
}
