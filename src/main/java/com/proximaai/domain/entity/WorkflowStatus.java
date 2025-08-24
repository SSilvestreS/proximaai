package com.proximaai.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_statuses")
@EntityListeners(AuditingEntityListener.class)
public class WorkflowStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 50)
    @Column(nullable = false)
    private String name;

    @Size(max = 200)
    @Column(length = 200)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id", nullable = false)
    private CustomWorkflow workflow;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "color")
    private String color; // Cor do status (ex: #FF5733)

    @Column(name = "icon")
    private String icon; // Ícone do status

    @Column(name = "is_initial")
    private Boolean isInitial = false; // Se é o status inicial do workflow

    @Column(name = "is_final")
    private Boolean isFinal = false; // Se é um status final

    @Column(name = "is_required")
    private Boolean isRequired = false; // Se é obrigatório passar por este status

    @Column(name = "estimated_duration_hours")
    private Integer estimatedDurationHours; // Duração estimada neste status

    @Column(name = "auto_assign_role")
    private String autoAssignRole; // Role que deve ser atribuído automaticamente

    @Column(name = "notification_template")
    private String notificationTemplate; // Template de notificação para este status

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public WorkflowStatus() {}

    public WorkflowStatus(String name, CustomWorkflow workflow) {
        this.name = name;
        this.workflow = workflow;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CustomWorkflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(CustomWorkflow workflow) {
        this.workflow = workflow;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Boolean getIsInitial() {
        return isInitial;
    }

    public void setIsInitial(Boolean isInitial) {
        this.isInitial = isInitial;
    }

    public Boolean getIsFinal() {
        return isFinal;
    }

    public void setIsFinal(Boolean isFinal) {
        this.isFinal = isFinal;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public Integer getEstimatedDurationHours() {
        return estimatedDurationHours;
    }

    public void setEstimatedDurationHours(Integer estimatedDurationHours) {
        this.estimatedDurationHours = estimatedDurationHours;
    }

    public String getAutoAssignRole() {
        return autoAssignRole;
    }

    public void setAutoAssignRole(String autoAssignRole) {
        this.autoAssignRole = autoAssignRole;
    }

    public String getNotificationTemplate() {
        return notificationTemplate;
    }

    public void setNotificationTemplate(String notificationTemplate) {
        this.notificationTemplate = notificationTemplate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
