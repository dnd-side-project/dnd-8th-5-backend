package com.dnd.modutime.core.adjustresult.domain;

import com.dnd.modutime.core.entity.Auditable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdjustmentResult implements Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String roomUuid;

    @Column(nullable = false)
    private boolean confirmation;

    @OneToMany(mappedBy = "adjustmentResult", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<CandidateDateTime> candidateDateTimes;

    private String createdBy;
    private LocalDateTime createdAt;
    private String modifiedBy;
    private LocalDateTime modifiedAt;

    public AdjustmentResult(String roomUuid,
                            List<CandidateDateTime> candidateDateTimes) {
        this.roomUuid = roomUuid;
        this.candidateDateTimes = candidateDateTimes;
        this.confirmation = false;
    }

    public void replace(List<CandidateDateTime> candidateDateTimes) {
        this.candidateDateTimes = candidateDateTimes;
    }

    public Long getId() {
        return id;
    }

    public String getRoomUuid() {
        return roomUuid;
    }

    public boolean isConfirmation() {
        return confirmation;
    }

    public List<CandidateDateTime> getCandidateDateTimes() {
        return candidateDateTimes;
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
