package com.dnd.modutime.domain.timetable;

import com.dnd.modutime.domain.timeblock.AvailableDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
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

    public void updateCount(List<AvailableDateTime> oldAvailableDateTimes,
                            List<AvailableDateTime> newAvailableDateTimes) {
        minusCount(oldAvailableDateTimes);
        plusCount(newAvailableDateTimes);
    }

    private void minusCount(List<AvailableDateTime> availableDateTimes) {
        availableDateTimes.forEach(
                availableDateTime -> dateInfos.forEach(
                        dateInfo -> dateInfo.minusCount(availableDateTime)
                )
        );
    }

    private void plusCount(List<AvailableDateTime> availableDateTimes) {
        availableDateTimes.forEach(
                availableDateTime -> dateInfos.forEach(
                        dateInfo -> dateInfo.plusCount(availableDateTime)
                )
        );
    }

    public List<DateInfo> getDateInfos() {
        return dateInfos;
    }
}
