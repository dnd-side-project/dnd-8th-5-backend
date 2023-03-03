package com.dnd.modutime.timeblock.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.AbstractAggregateRoot;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"room_uuid", "participant_name"})})
public class TimeBlock extends AbstractAggregateRoot<TimeBlock> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "room_uuid", nullable = false)
    private String roomUuid;

    @Column(name = "participant_name", nullable = false)
    private String participantName;

    @OneToMany(mappedBy = "timeBlock", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    private List<AvailableDateTime> availableDateTimes = List.of();

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

    public Long getId() {
        return id;
    }

    public String getParticipantName() {
        return participantName;
    }

    public List<AvailableDateTime> getAvailableDateTimes() {
        return availableDateTimes;
    }
}
