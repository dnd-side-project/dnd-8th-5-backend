package com.dnd.modutime.room.domain;

import static javax.persistence.GenerationType.IDENTITY;

import com.dnd.modutime.timetable.application.RoomCreationEvent;
import com.dnd.modutime.util.TimeProvider;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PostPersist;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.AbstractAggregateRoot;

@Entity
@NoArgsConstructor
public class Room extends AbstractAggregateRoot<Room> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column
    private LocalTime startTime;

    @Column
    private LocalTime endTime;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    @JoinColumn(
            name = "room_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_room_date_room_id_ref_room_id")
    )
    private List<RoomDate> roomDates;

    @Column
    private Integer headCount;

    @Column(nullable = false, unique = true)
    private String uuid;

    @Column
    private LocalDateTime deadLine;

    public Room(String title,
                LocalTime startTime,
                LocalTime endTime,
                List<RoomDate> roomDates,
                Integer headCount,
                LocalDateTime deadLine,
                TimeProvider timeProvider) {

        validateTitle(title);
        validateStartAndEndTime(startTime, endTime);
        validateRoomDates(roomDates);
        validateHeadCount(headCount);
        validateDeadLine(deadLine, timeProvider);

        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomDates = roomDates;
        this.headCount = headCount;
        this.uuid = UUID.randomUUID().toString();
        this.deadLine = deadLine;
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("방의 제목은 빈문자일 수 없습니다.");
        }
    }

    private void validateStartAndEndTime(LocalTime startTime, LocalTime endTime) {
        if (startTime == null && endTime == null) {
            return;
        }

        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("시작시간과 끝나는 시간은 하나만 null일 수 없습니다.");
        }


        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("시작시간은 끝나는 시간보다 작아야 합니다.");
        }
    }

    private void validateHeadCount(Integer headCount) {
        if (headCount == null) {
            return;
        }

        if (headCount < 0) {
            throw new IllegalArgumentException("방 참여 인원은 음수일 수 없습니다.");
        }
    }

    private void validateRoomDates(List<RoomDate> roomDates) {
        if (roomDates == null) {
            throw new IllegalArgumentException("날짜는 null일 수 없습니다.");
        }
        if (roomDates.isEmpty()) {
            throw new IllegalArgumentException("날짜는 최소 1개이상 존재해야 합니다.");
        }
    }

    private void validateDeadLine(LocalDateTime deadLine,
                                  TimeProvider timeProvider) {
        if (deadLine == null) {
            return;
        }

        LocalDateTime now = timeProvider.getCurrentLocalDateTime();
        if (!now.isBefore(deadLine)) {
            throw new IllegalArgumentException("마감시간은 현재시간 이후여야 합니다.");
        }
    }

    @PostPersist
    private void registerCreateEvent() {
        registerEvent(new RoomCreationEvent(uuid));
    }

    public boolean containsAllDates(List<RoomDate> roomDates) {
        return roomDates.stream()
                .allMatch(this::containsDate);
    }

    private boolean containsDate(RoomDate roomDate) {
        return this.roomDates.stream()
                .anyMatch(it -> it.isSameDate(roomDate));
    }

    public boolean hasStartAndEndTime() {
        return !(startTime == null && endTime == null);
    }

    public boolean includeTime(LocalTime time) {
        return (!startTime.isAfter(time)) && (time.isBefore(endTime));
    }

    public String getTitle() {
        return title;
    }

    public LocalTime getStartTimeOrNull() {
        return startTime;
    }

    public LocalTime getEndTimeOrNull() {
        return endTime;
    }

    public List<RoomDate> getRoomDates() {
        return roomDates;
    }

    public Integer getHeadCountOrNull() {
        return headCount;
    }

    public String getUuid() {
        return uuid;
    }

    public LocalDateTime getDeadLineOrNull() {
        return deadLine;
    }
}
