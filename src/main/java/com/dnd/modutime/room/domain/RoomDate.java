package com.dnd.modutime.room.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomDate {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    public RoomDate(LocalDate date) {
        this.date = date;
    }

    public boolean isSameDate(RoomDate roomDate) {
        return this.date.equals(roomDate.getDate());
    }

    public LocalDate getDate() {
        return date;
    }
}
