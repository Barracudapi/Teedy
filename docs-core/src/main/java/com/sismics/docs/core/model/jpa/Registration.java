package com.sismics.docs.core.model.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "T_REGISTRATION")
public class Registration {
    @Id
    @Column(name = "REG_ID_C", length = 36)
    private String id;

    @Column(name = "REG_TOKEN_C", nullable = false, length = 100)
    private String token;

    @Column(name = "REG_IP_C", nullable = false, length = 45)
    private String ip;

    @Column(name = "REG_CREATED_AT_D", nullable = false)
    private Date createdAt;

    @Column(name = "REG_USERNAME_C", length = 50)
    private String username;

    @Column(name = "REG_PASSWORD_C", length = 50)
    private String password;

    @Column(name = "REG_STATUS_C", nullable = false, length = 20)
    private String status = "PENDING";

    public Registration() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = new Date();
    }

    public Registration(String token, String ip, String username, String password) {
        this();
        this.token = token;
        this.ip = ip;
        this.username = username;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public Registration setId(String id) {
        this.id = id;
        return this;
    }

    public String getToken() {
        return token;
    }

    public Registration setToken(String token) {
        this.token = token;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public Registration setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getCreatedAtToString() {
        return createdAt.toString();
    }

    public Registration setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public Registration setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Registration setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public Registration setStatus(String status) {
        this.status = status;
        return this;
    }
}