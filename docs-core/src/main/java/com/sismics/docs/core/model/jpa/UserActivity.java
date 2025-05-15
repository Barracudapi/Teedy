package com.sismics.docs.core.model.jpa;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "USER_ACTIVITY")
public class UserActivity {
    @Id
    @Column(name = "ID", length = 36)
    private String id;
    
    @Column(name = "USER_ID", length = 36, nullable = false)
    private String userId;
    
    @Column(name = "ENTITY_ID", length = 36)
    private String entityId;
    
    @Column(name = "PROGRESS", nullable = false)
    private Integer progress;
    
    @Column(name = "DEADLINE")
    private Date deadline;
    
    @Column(name = "COMPLETED_DATE")
    private Date completedDate;
    
    @Column(name = "CREATE_DATE", nullable = false)
    private Date createDate;
    
    @Column(name = "DELETE_DATE")
    private Date deleteDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Date getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(Date completedDate) {
        this.completedDate = completedDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(Date deleteDate) {
        this.deleteDate = deleteDate;
    }
} 