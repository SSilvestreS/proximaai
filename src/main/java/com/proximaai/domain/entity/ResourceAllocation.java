package com.proximaai.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "resource_allocations")
@EntityListeners(AuditingEntityListener.class)
public class ResourceAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task; // Opcional - pode ser alocação geral no projeto

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AllocationType type = AllocationType.HUMAN_RESOURCE;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "allocated_hours_per_day")
    @Positive
    private Integer allocatedHoursPerDay = 8;

    @Column(name = "allocated_hours_total")
    @Positive
    private Integer allocatedHoursTotal;

    @Column(name = "actual_hours_worked")
    private Integer actualHoursWorked = 0;

    @Column(name = "hourly_rate")
    @Positive
    private BigDecimal hourlyRate;

    @Column(name = "total_cost")
    private BigDecimal totalCost;

    @Column(name = "actual_cost")
    private BigDecimal actualCost = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AllocationStatus status = AllocationStatus.PLANNED;

    @Column(name = "skills_required")
    private String skillsRequired; // Comma-separated skills

    @Column(name = "notes")
    private String notes;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Enums
    public enum AllocationType {
        HUMAN_RESOURCE,    // Recursos humanos
        EQUIPMENT,         // Equipamentos
        SOFTWARE,          // Licenças de software
        INFRASTRUCTURE,    // Infraestrutura
        EXTERNAL_SERVICE   // Serviços externos
    }

    public enum AllocationStatus {
        PLANNED,           // Planejado
        ACTIVE,            // Ativo
        COMPLETED,         // Concluído
        CANCELLED,         // Cancelado
        ON_HOLD            // Em espera
    }

    // Constructors
    public ResourceAllocation() {}

    public ResourceAllocation(Project project, User user, LocalDate startDate) {
        this.project = project;
        this.user = user;
        this.startDate = startDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public AllocationType getType() {
        return type;
    }

    public void setType(AllocationType type) {
        this.type = type;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getAllocatedHoursPerDay() {
        return allocatedHoursPerDay;
    }

    public void setAllocatedHoursPerDay(Integer allocatedHoursPerDay) {
        this.allocatedHoursPerDay = allocatedHoursPerDay;
    }

    public Integer getAllocatedHoursTotal() {
        return allocatedHoursTotal;
    }

    public void setAllocatedHoursTotal(Integer allocatedHoursTotal) {
        this.allocatedHoursTotal = allocatedHoursTotal;
    }

    public Integer getActualHoursWorked() {
        return actualHoursWorked;
    }

    public void setActualHoursWorked(Integer actualHoursWorked) {
        this.actualHoursWorked = actualHoursWorked;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BigDecimal getActualCost() {
        return actualCost;
    }

    public void setActualCost(BigDecimal actualCost) {
        this.actualCost = actualCost;
    }

    public AllocationStatus getStatus() {
        return status;
    }

    public void setStatus(AllocationStatus status) {
        this.status = status;
    }

    public String getSkillsRequired() {
        return skillsRequired;
    }

    public void setSkillsRequired(String skillsRequired) {
        this.skillsRequired = skillsRequired;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
