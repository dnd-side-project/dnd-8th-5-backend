package com.dnd.modutime.core.participant.domain;

import com.dnd.modutime.core.entity.Auditable;
import com.dnd.modutime.core.timeblock.application.ParticipantCreationEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"room_uuid", "name"})})
public class Participant extends AbstractAggregateRoot<Participant> implements Auditable {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[0-9]{4}$");

    @Getter
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Getter
    @Column(name = "room_uuid", nullable = false)
    private String roomUuid;

    @Getter
    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String password;

    @Embedded
    private Email email;

    @Getter
    @Column(name = "user_id")
    private Long userId;

    private String createdBy;
    private LocalDateTime createdAt;
    private String modifiedBy;
    private LocalDateTime modifiedAt;

    public Participant(String roomUuid, String name, String password) {
        validateRoomUuid(roomUuid);
        validateName(name);
        validatePassword(password);

        this.roomUuid = roomUuid;
        this.name = name;
        this.password = password;
        this.email = null;
    }

    private static void validateRoomUuid(String roomUuid) {
        if (roomUuid == null) {
            throw new IllegalArgumentException("roomUuid는 null일 수 없습니다");
        }
    }

    public static Participant of(String roomUuid, String name, Long userId) {
        validateRoomUuid(roomUuid);
        validateName(name);
        if (userId == null) {
            throw new IllegalArgumentException("userId는 null일 수 없습니다");
        }
        var participant = new Participant();
        participant.roomUuid = roomUuid;
        participant.name = name;
        participant.password = null;
        participant.userId = userId;
        participant.email = null;
        return participant;
    }

    private static void validateName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("이름은 null일 수 없습니다");
        }
    }

    private void validatePassword(String password) {
        if (isRightPassword(password)) {
            throw new IllegalArgumentException("비밀번호는 4자리 숫자여야 합니다.");
        }
    }

    @PostPersist
    private void registerCreateEvent() {
        registerEvent(new ParticipantCreationEvent(this.roomUuid, this.name));
    }

    @PreRemove
    private void registerRemovedEvent() {
        registerEvent(new ParticipantRemovedEvent(this.roomUuid, this.name));
    }

    private boolean isRightPassword(String password) {
        return !PASSWORD_PATTERN.matcher(password).find();
    }

    public void registerEmail(Email email) {
        this.email = email;
    }

    public boolean hasEmail() {
        return email != null;
    }

    public boolean matchPassword(String password) {
        if (this.password == null) return false;
        return this.password.equals(password);
    }

    public boolean isRegisteredUser() {
        return this.userId != null;
    }

    public Email getEmailOrNull() {
        return email;
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