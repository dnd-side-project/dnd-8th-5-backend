package com.dnd.modutime.core.adjustresult.domain;

import com.dnd.modutime.core.entity.Auditable;
import com.dnd.modutime.core.participant.domain.Participant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CandidateDateTime implements Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adjustment_result_id")
    private AdjustmentResult adjustmentResult;

    @Column(nullable = false)
    private LocalDateTime startDateTime; // "2022-02-11 00:00:00"

    @Column(nullable = false)
    private LocalDateTime endDateTime; // "2022-02-11 00:00:00"

    // TODO: hasTime

    @Column(nullable = false)
    private Boolean isConfirmed;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    @JoinColumn(
            name = "candidate_date_time_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_cdt_participant_name_cdt_id_ref_cdt_id")
    )
    private List<CandidateDateTimeParticipantName> participantNames;

    private String createdBy;
    private LocalDateTime createdAt;
    private String modifiedBy;
    private LocalDateTime modifiedAt;

    public CandidateDateTime(AdjustmentResult adjustmentResult,
                             LocalDateTime startDateTime,
                             LocalDateTime endDateTime,
                             Boolean isConfirmed,
                             List<CandidateDateTimeParticipantName> participantNames) {
        this.adjustmentResult = adjustmentResult;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.isConfirmed = isConfirmed;
        this.participantNames = participantNames;
    }

    public long calculateTerm() {
        return ChronoUnit.SECONDS.between(endDateTime, startDateTime);
    }

    public void makeEntity(AdjustmentResult adjustmentResult) {
        this.adjustmentResult = adjustmentResult;
        this.isConfirmed = false;
    }

    public int getParticipantSize() {
        return participantNames.size();
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public Boolean isConfirmed() {
        return isConfirmed;
    }

    public List<CandidateDateTimeParticipantName> getParticipantNames() {
        return participantNames;
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

    public boolean containsExactly(final List<Participant> participants) {
        if (this.participantNames.size() != participants.size()) {
            return false;
        }

        var participantNames = this.participantNames.stream()
                .map(CandidateDateTimeParticipantName::getName)
                .sorted()
                .toList();

        var inputParticipantNames = participants.stream()
                .map(Participant::getName)
                .sorted()
                .toList();

        return participantNames.equals(inputParticipantNames);
    }
}
