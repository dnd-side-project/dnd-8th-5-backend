package com.dnd.modutime.core.room.domain;

import com.dnd.modutime.core.entity.Auditable;
import com.dnd.modutime.util.TimeProvider;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room extends AbstractAggregateRoot<Room> implements Auditable {

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

    private String createdBy;
    private LocalDateTime createdAt;
    private String modifiedBy;
    private LocalDateTime modifiedAt;

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

        if (startTime == endTime) {
            throw new IllegalArgumentException("시작시간과 끝나는 시간은 같을 수 없습니다.");
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
        if (startTime.isBefore(endTime)) {
            return (time.equals(startTime) || time.isAfter(startTime)) && time.isBefore(endTime);
        }
        return (time.equals(startTime) || time.isAfter(startTime)) || time.isBefore(endTime);
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

    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Override
    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}
