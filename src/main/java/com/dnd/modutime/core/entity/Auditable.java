package com.dnd.modutime.core.entity;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public interface Auditable {
    @CreatedBy
    @Column(name = "created_by", columnDefinition = "VARCHAR(50) COMMENT '생성자'")
    void setCreatedBy(String createdBy);

    @CreatedDate
    @Column(name = "created_at", columnDefinition = "DATETIME(6) COMMENT '생성일시'")
    void setCreatedAt(LocalDateTime createdAt);

    @LastModifiedBy
    @Column(name = "modified_by", columnDefinition = "VARCHAR(50) COMMENT '수정자'")
    void setModifiedBy(String modifiedBy);

    @LastModifiedDate
    @Column(name = "modified_at", columnDefinition = "DATETIME(6) COMMENT '수정일시'")
    void setModifiedAt(LocalDateTime modifiedAt);
}
