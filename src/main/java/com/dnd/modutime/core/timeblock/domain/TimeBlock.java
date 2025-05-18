package com.dnd.modutime.core.timeblock.domain;

import com.dnd.modutime.core.entity.Auditable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * TimeBlock은 '참여자의 시간 블록'을 조작하는 엔티티입니다.
 * 참여자는 자신의 시간 블록을 등록하고, 수정하고, 삭제할 수 있습니다.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"room_uuid", "participant_name"})})
public class TimeBlock extends AbstractAggregateRoot<TimeBlock> implements Auditable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "room_uuid", nullable = false)
    private String roomUuid;

    @Column(name = "participant_name", nullable = false)
    private String participantName;

    @OneToMany(mappedBy = "timeBlock", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    private List<AvailableDateTime> availableDateTimes = List.of();

    private String createdBy;
    private LocalDateTime createdAt;
    private String modifiedBy;
    private LocalDateTime modifiedAt;

    public TimeBlock(String roomUuid,
                     String participantName) {
        validateRoomUuid(roomUuid);
        validateParticipantName(participantName);

        this.roomUuid = roomUuid;
        this.participantName = participantName;
    }

    private void validateParticipantName(String participantName) {
        if (participantName == null) {
            throw new IllegalArgumentException("participantName은 null일 수 없습니다.");
        }
    }

    private void validateRoomUuid(String roomUuid) {
        if (roomUuid == null) {
            throw new IllegalArgumentException("roomUuid는 null일 수 없습니다.");
        }
    }

    public void replace(List<AvailableDateTime> availableDateTimes) {
        List<AvailableDateTime> oldAvailableDateTimes = this.availableDateTimes;
        this.availableDateTimes = availableDateTimes;
        registerEvent(new TimeBlockReplaceEvent(roomUuid, oldAvailableDateTimes, availableDateTimes, participantName));
    }

    @PreRemove
    private void registerRemovedEvent() {
        registerEvent(new TimeBlockRemovedEvent());
    }

    public Long getId() {
        return id;
    }

    public String getParticipantName() {
        return participantName;
    }

    public List<AvailableDateTime> getAvailableDateTimes() {
        return availableDateTimes;
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
