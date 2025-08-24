package com.proximaai.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@EntityListeners(AuditingEntityListener.class)
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "entity_type", nullable = false)
    private String entityType;

    @NotNull
    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @NotBlank
    @Column(name = "action", nullable = false)
    private String action; // CREATE, UPDATE, DELETE, VIEW, etc.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "user_ip")
    private String userIp;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "old_values", columnDefinition = "TEXT")
    private String oldValues; // JSON representation of old state

    @Column(name = "new_values", columnDefinition = "TEXT")
    private String newValues; // JSON representation of new state

    @Column(name = "changes", columnDefinition = "TEXT")
    private String changes; // JSON representation of what changed

    @Column(name = "reason")
    private String reason; // Optional reason for the change

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "request_id")
    private String requestId; // For tracking specific requests

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public AuditLog() {}

    public AuditLog(String entityType, Long entityId, String action, User user) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.action = action;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getOldValues() {
        return oldValues;
    }

    public void setOldValues(String oldValues) {
        this.oldValues = oldValues;
    }

    public String getNewValues() {
        return newValues;
    }

    public void setNewValues(String newValues) {
        this.newValues = newValues;
    }

    public String getChanges() {
        return changes;
    }

    public void setChanges(String changes) {
        this.changes = changes;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Helper methods
    public boolean isCreateAction() {
        return "CREATE".equalsIgnoreCase(action);
    }

    public boolean isUpdateAction() {
        return "UPDATE".equalsIgnoreCase(action);
    }

    public boolean isDeleteAction() {
        return "DELETE".equalsIgnoreCase(action);
    }

    public boolean isViewAction() {
        return "VIEW".equalsIgnoreCase(action);
    }

    public String getUsername() {
        return user != null ? user.getUsername() : "SYSTEM";
    }

    // Predefined actions
    public static final class Actions {
        public static final String CREATE = "CREATE";
        public static final String UPDATE = "UPDATE";
        public static final String DELETE = "DELETE";
        public static final String VIEW = "VIEW";
        public static final String LOGIN = "LOGIN";
        public static final String LOGOUT = "LOGOUT";
        public static final String EXPORT = "EXPORT";
        public static final String IMPORT = "IMPORT";
        public static final String ASSIGN = "ASSIGN";
        public static final String STATUS_CHANGE = "STATUS_CHANGE";
        public static final String PRIORITY_CHANGE = "PRIORITY_CHANGE";
    }

    // Predefined entity types
    public static final class EntityTypes {
        public static final String USER = "USER";
        public static final String TEAM = "TEAM";
        public static final String PROJECT = "PROJECT";
        public static final String TASK = "TASK";
        public static final String COMMENT = "COMMENT";
        public static final String TIME_LOG = "TIME_LOG";
        public static final String NOTIFICATION = "NOTIFICATION";
    }
}
