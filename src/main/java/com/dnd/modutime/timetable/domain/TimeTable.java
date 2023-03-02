package com.dnd.modutime.timetable.domain;

import com.dnd.modutime.timeblock.domain.AvailableDateTime;
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
import org.springframework.data.domain.AbstractAggregateRoot;

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

    public void updateParticipantName(List<AvailableDateTime> oldAvailableDateTimes,
                                      List<AvailableDateTime> newAvailableDateTimes,
                                      String participantName) {
        removeParticipantName(oldAvailableDateTimes, participantName);
        addParticipantName(newAvailableDateTimes, participantName);
        registerEvent(new TimeTableReplaceEvent(roomUuid, dateInfos));
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

    public List<DateInfo> getDateInfos() {
        return dateInfos;
    }
}
