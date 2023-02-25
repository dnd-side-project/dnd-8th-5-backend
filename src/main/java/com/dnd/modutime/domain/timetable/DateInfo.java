package com.dnd.modutime.domain.timetable;

import com.dnd.modutime.domain.timeblock.AvailableDateTime;
import com.dnd.modutime.domain.timeblock.AvailableTime;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class DateInfo {

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
            name = "time_table_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_time_info_date_info_id_ref_date_info_id")
    )
    private List<TimeInfo> timeInfos;

    public DateInfo(TimeTable timeTable,
                    LocalDate date,
                    List<TimeInfo> timeInfos) {
        this.timeTable = timeTable;
        this.date = date;
        this.timeInfos = timeInfos;
    }

    public void minusCount(AvailableDateTime availableDateTime) {
        if (!date.isEqual(availableDateTime.getDate())) {
            return;
        }
        List<AvailableTime> timesOrNull = availableDateTime.getTimesOrNull();
        if (timesOrNull == null) {
            validateTimeInfoIsEmpty();
            TimeInfo timeInfo = timeInfos.get(0);
            timeInfo.minusCount();
            return;
        }
        timeInfos.forEach(
                timeInfo -> timesOrNull.forEach(availableTime -> timeInfo.minusCountIfSameTime(availableTime.getTime()))
        );
    }

    public void plusCount(AvailableDateTime availableDateTime) {
        if (!date.isEqual(availableDateTime.getDate())) {
            return;
        }
        List<AvailableTime> timesOrNull = availableDateTime.getTimesOrNull();
        if (timesOrNull == null) {
            validateTimeInfoIsEmpty();
            TimeInfo timeInfo = timeInfos.get(0);
            timeInfo.plusCount();
            return;
        }
        timeInfos.forEach(
                timeInfo -> timesOrNull.forEach(availableTime -> timeInfo.plusCountIfSameTime(availableTime.getTime()))
        );
    }

    private void validateTimeInfoIsEmpty() {
        if (timeInfos.isEmpty()) {
            throw new IllegalArgumentException("timeInfo가 비어있을 수 없습니다.");
        }
    }

    public LocalDate getDate() {
        return date;
    }

    public List<TimeInfo> getTimeInfos() {
        return timeInfos;
    }
}
