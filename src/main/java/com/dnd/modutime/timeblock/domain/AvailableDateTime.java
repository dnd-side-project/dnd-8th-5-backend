package com.dnd.modutime.timeblock.domain;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class AvailableDateTime {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_block_id")
    private TimeBlock timeBlock;

    @Column(nullable = false)
    private LocalDate date;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    @JoinColumn(
            name = "available_date_time_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_available_time_available_date_time_id_ref_available_date_time_id")
    )
    private List<AvailableTime> times;

    public AvailableDateTime(TimeBlock timeBlock,
                             LocalDate date,
                             List<AvailableTime> times) {
        validateDate(date);
        this.timeBlock = timeBlock;
        this.date = date;
        this.times = times;
    }

    private void validateDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("date는 null일 수 없습니다.");
        }
    }

    public boolean hasTime() {
        return times != null;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<AvailableTime> getTimesOrNull() {
        return times;
    }
}
