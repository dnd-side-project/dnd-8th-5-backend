package com.dnd.modutime.core.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @CreatedBy
    @Column(name = "created_by", columnDefinition = "VARCHAR(50) COMMENT '생성자'")
    private String createdBy;

    @CreatedDate
    @Column(name = "created_at", columnDefinition = "DATETIME(6) COMMENT '생성일시'")
    private LocalDateTime createdAt;

    @LastModifiedBy
    @Column(name = "modified_by", columnDefinition = "VARCHAR(50) COMMENT '수정자'")
    private String modifiedBy;

    @LastModifiedDate
    @Column(name = "modified_at", columnDefinition = "DATETIME(6) COMMENT '수정일시'")
    private LocalDateTime modifiedAt;

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreatedAt(final LocalDateTime creationDate) {
        this.createdAt = creationDate;
    }

    public void setModifiedBy(final String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public void setModifiedAt(final LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}
