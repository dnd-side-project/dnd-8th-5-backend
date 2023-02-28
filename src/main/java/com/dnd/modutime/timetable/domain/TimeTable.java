package com.dnd.modutime.timetable.domain;

import com.dnd.modutime.timeblock.domain.AvailableDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String roomUuid;

    @OneToMany(mappedBy = "timeTable", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<DateInfo> dateInfos = List.of();

    public TimeTable(String roomUuid) {
        this.roomUuid = roomUuid;
    }

    public void replaceDateInfos(List<DateInfo> dateInfos) {
        this.dateInfos = dateInfos;
    }

    public void updateParticipantName(List<AvailableDateTime> oldAvailableDateTimes,
                                      List<AvailableDateTime> newAvailableDateTimes,
                                      String participantName) {
//        minusCount(oldAvailableDateTimes);
//        plusCount(newAvailableDateTimes);

        removeParticipantName(oldAvailableDateTimes, participantName);
        addParticipantName(newAvailableDateTimes, participantName);
    }

    private void removeParticipantName(List<AvailableDateTime> availableDateTimes, String participantName) {
        availableDateTimes.forEach(
                availableDateTime -> dateInfos.forEach(
                        dateInfo -> dateInfo.removeParticipantNameIfSameDate(availableDateTime, participantName)
                )
        );
    }

    private void addParticipantName(List<AvailableDateTime> availableDateTimes, String participantName) {
        availableDateTimes.forEach(
                availableDateTime -> dateInfos.forEach(
                        dateInfo -> dateInfo.addParticipantNameIfSameDate(availableDateTime, participantName)
                )
        );
    }

    public List<TimeInfo> getTimeInfosByAvailableDateTimesAndParticipantName(List<AvailableDateTime> availableDateTimes,
                                                                             String participantName) {
        for (DateInfo dateInfo : dateInfos) {
            for (AvailableDateTime availableDateTime : availableDateTimes) {
                List<TimeInfo> timeInfos = dateInfo.getByDateAndTimesAndParticipantName(availableDateTime.getDate(),
                        availableDateTime.getTimesOrNull(), participantName);

            }
        }

        // availableDateTimes 의 각 date중 timeTable이 갖고있는 dateInfo의 date와 맞는 것들중
        // 시간, 참여자 이름

        // TimeTable이 가지고 있는 모든 DateInfo들을 돌면서
        // DateInfo에서
        return ;
    }

    public List<DateInfo> getDateInfos() {
        return dateInfos;
    }
}
