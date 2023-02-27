package com.dnd.modutime.timetable.domain;

import java.time.LocalTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class TimeInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalTime time;

    @Column(nullable = false)
    private int count;

    public TimeInfo(LocalTime time, int count) {
        this.time = time;
        this.count = count;
    }

    public void minusCountIfSameTime(LocalTime time) {
        if (this.time.equals(time)) {
            minusCount();
        }
    }

    public void minusCount() {
        validatePossibleMinus();
        count--;
    }

    private void validatePossibleMinus() {
        if (count < 1) {
            throw new IllegalArgumentException("count는 음수가 될 수 없습니다.");
        }
    }

    public void plusCountIfSameTime(LocalTime time) {
        if (this.time.equals(time)) {
            count++;
        }
    }

    public void plusCount() {
        count++;
    }

    public LocalTime getTime() {
        return time;
    }

    public int getCount() {
        return count;
    }
}
