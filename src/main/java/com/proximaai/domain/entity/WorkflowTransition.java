package com.proximaai.domain.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_transitions")
@EntityListeners(AuditingEntityListener.class)
public class WorkflowTransition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id", nullable = false)
    private CustomWorkflow workflow;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_status_id", nullable = false)
    private WorkflowStatus fromStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_status_id", nullable = false)
    private WorkflowStatus toStatus;

    @Column(name = "transition_name")
    private String transitionName; // Nome da transição (ex: "Aprovar", "Rejeitar")

    @Column(name = "description")
    private String description; // Descrição da transição

    @Column(name = "is_automatic")
    private Boolean isAutomatic = false; // Se a transição é automática

    @Column(name = "requires_approval")
    private Boolean requiresApproval = false; // Se requer aprovação

    @Column(name = "required_roles")
    private String requiredRoles; // Roles que podem executar esta transição (comma-separated)

    @Column(name = "conditions")
    private String conditions; // Condições para permitir a transição (JSON)

    @Column(name = "actions")
    private String actions; // Ações a serem executadas na transição (JSON)

    @Column(name = "estimated_duration_hours")
    private Integer estimatedDurationHours; // Tempo estimado para esta transição

    @Column(name = "is_reversible")
    private Boolean isReversible = false; // Se a transição pode ser revertida

    @Column(name = "color")
    private String color; // Cor da transição

    @Column(name = "icon")
    private String icon; // Ícone da transição

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public WorkflowTransition() {}

    public WorkflowTransition(CustomWorkflow workflow, WorkflowStatus fromStatus, WorkflowStatus toStatus) {
        this.workflow = workflow;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CustomWorkflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(CustomWorkflow workflow) {
        this.workflow = workflow;
    }

    public WorkflowStatus getFromStatus() {
        return fromStatus;
    }

    public void setFromStatus(WorkflowStatus fromStatus) {
        this.fromStatus = fromStatus;
    }

    public WorkflowStatus getToStatus() {
        return toStatus;
    }

    public void setToStatus(WorkflowStatus toStatus) {
        this.toStatus = toStatus;
    }

    public String getTransitionName() {
        return transitionName;
    }

    public void setTransitionName(String transitionName) {
        this.transitionName = transitionName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsAutomatic() {
        return isAutomatic;
    }

    public void setIsAutomatic(Boolean isAutomatic) {
        this.isAutomatic = isAutomatic;
    }

    public Boolean getRequiresApproval() {
        return requiresApproval;
    }

    public void setRequiresApproval(Boolean requiresApproval) {
        this.requiresApproval = requiresApproval;
    }

    public String getRequiredRoles() {
        return requiredRoles;
    }

    public void setRequiredRoles(String requiredRoles) {
        this.requiredRoles = requiredRoles;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

    public Integer getEstimatedDurationHours() {
        return estimatedDurationHours;
    }

    public void setEstimatedDurationHours(Integer estimatedDurationHours) {
        this.estimatedDurationHours = estimatedDurationHours;
    }

    public Boolean getIsReversible() {
        return isReversible;
    }

    public void setIsReversible(Boolean isReversible) {
        this.isReversible = isReversible;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
