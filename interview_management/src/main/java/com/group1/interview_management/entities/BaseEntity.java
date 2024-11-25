package com.group1.interview_management.entities;

import java.time.LocalDateTime;

import jakarta.persistence.PrePersist;

import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(nullable = false, updatable = true)
    @AccessType(AccessType.Type.PROPERTY)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedDate;

    @CreatedBy
    @Column(nullable = false, updatable = true)
    @AccessType(AccessType.Type.PROPERTY)
    private Integer createdBy;

    @LastModifiedBy
    @Column(nullable = false)
    private Integer modifiedBy;

    @Column(nullable = false)
    private Boolean deleteFlag = false;

    @PrePersist
    protected void onCreate() {
        this.modifiedDate = this.createdDate;
        this.modifiedBy = this.createdBy;
    }

    /**
     * Set created by manually instead of using @CreatedBy
     * We are overriding the createdBy field because we want to set createdBy manually
     *
     * @param createdBy
     */
    public void setCreatedBy(Integer createdBy) {
        if (this.createdBy == null) {
            this.createdBy = createdBy;
        }
    }

    public void updateCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Set created date manually instead of using @CreatedDate
     * We are overriding the createdDate field because we want to set createdDate manually
     *
     * @param createdDate
     */
    public void setCreatedDate(LocalDateTime createdDate) {
        if (this.createdDate == null) {
            this.createdDate = createdDate;
        }
    }
    public void updateCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}


