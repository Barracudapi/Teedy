package com.sismics.docs.core.dao.criteria;

public class UserActivityCriteria {
    private String userId;
    private String entityId;

    public String getUserId() {
        return userId;
    }

    public UserActivityCriteria setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getEntityId() {
        return entityId;
    }

    public UserActivityCriteria setEntityId(String entityId) {
        this.entityId = entityId;
        return this;
    }
} 