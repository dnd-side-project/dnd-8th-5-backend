package com.dnd.modutime.domain.timetable;

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

    public TimeInfo(final LocalTime time, final int count) {
        this.time = time;
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
