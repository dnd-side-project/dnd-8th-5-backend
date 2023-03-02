package com.dnd.modutime.adjustresult.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CandidateDateTime {

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
            foreignKey = @ForeignKey(name = "fk_candidate_date_time_participant_name_candidate_date_time_id_ref_candidate_date_time_id")
    )
    private List<CandidateDateTimeParticipantName> participantNames;

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
}
