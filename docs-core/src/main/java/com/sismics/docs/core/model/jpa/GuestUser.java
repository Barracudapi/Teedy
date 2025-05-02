package com.sismics.docs.core.model.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;
import java.util.UUID;

/**
 * Guest user entity.
 */
@Entity
@Table(name = "guest_user")
public class GuestUser {
    @Id
    @Column(name = "ID", length = 36)
    private String identifier;
    
    @Column(name = "ACCESS_TOKEN", nullable = false, length = 100)
    private String accessToken;
    
    @Column(name = "CLIENT_IP", nullable = false, length = 45)
    private String clientIp;
    
    @Column(name = "CREATED_DATE", nullable = false)
    private Date createdDate;
    
    @Column(name = "STATUS", nullable = false, length = 20)
    private String requestStatus = "PENDING";
    
    public GuestUser() {
        this.identifier = UUID.randomUUID().toString();
        this.createdDate = new Date();
    }
    
    public GuestUser(String accessToken, String clientIp) {
        this();
        this.accessToken = accessToken;
        this.clientIp = clientIp;
    }
    
    // Getters and setters
    
    public String getIdentifier() {
        return identifier;
    }
    
    public GuestUser setIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public GuestUser setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }
    
    public String getClientIp() {
        return clientIp;
    }
    
    public GuestUser setClientIp(String clientIp) {
        this.clientIp = clientIp;
        return this;
    }
    
    public Date getCreatedDate() {
        return createdDate;
    }
    
    public GuestUser setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
        return this;
    }
    
    public String getRequestStatus() {
        return requestStatus;
    }
    
    public GuestUser setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
        return this;
    }
    
    public boolean isPending() {
        return "PENDING".equals(requestStatus);
    }
    
    public String getId() {
        return identifier;
    }
    
    public GuestUser setId(String id) {
        this.identifier = id;
        return this;
    }

    public String getToken() {
        return accessToken;
    }

    public GuestUser setToken(String token) {
        this.accessToken = token;
        return this;
    }

    public String getIp() {
        return clientIp;
    }

    public GuestUser setIp(String ip) {
        this.clientIp = ip;
        return this;
    }

    public Date getTimestamp() {
        return createdDate;
    }
    
    public GuestUser setTimestamp(Date timestamp) {
        this.createdDate = timestamp;
        return this;
    }
    
    public String getStatus() {
        return requestStatus;
    }
    
    public GuestUser setStatus(String status) {
        this.requestStatus = status;
        return this;
    }
}