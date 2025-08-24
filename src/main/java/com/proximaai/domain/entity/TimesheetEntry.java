package com.proximaai.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "timesheet_entries")
@EntityListeners(AuditingEntityListener.class)
public class TimesheetEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timesheet_id", nullable = false)
    private Timesheet timesheet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task; // Opcional - pode ser tempo geral

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "duration_minutes")
    @Positive
    private Integer durationMinutes; // Duração em minutos

    @Column(name = "description")
    private String description; // Descrição da atividade

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Timesheet.WorkType workType = Timesheet.WorkType.DEVELOPMENT;

    @Column(name = "activity_type")
    private String activityType; // Tipo específico de atividade

    @Column(name = "is_billable")
    private Boolean isBillable = true; // Se é faturável

    @Column(name = "hourly_rate")
    private BigDecimal hourlyRate;

    @Column(name = "amount")
    private BigDecimal amount; // Valor calculado

    @Column(name = "notes")
    private String notes; // Notas adicionais

    @Column(name = "tags")
    private String tags; // Tags para categorização

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public TimesheetEntry() {}

    public TimesheetEntry(Timesheet timesheet, LocalTime startTime) {
        this.timesheet = timesheet;
        this.startTime = startTime;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timesheet getTimesheet() {
        return timesheet;
    }

    public void setTimesheet(Timesheet timesheet) {
        this.timesheet = timesheet;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timesheet.WorkType getWorkType() {
        return workType;
    }

    public void setWorkType(Timesheet.WorkType workType) {
        this.workType = workType;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public Boolean getIsBillable() {
        return isBillable;
    }

    public void setIsBillable(Boolean isBillable) {
        this.isBillable = isBillable;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Helper methods
    public void calculateDuration() {
        if (startTime != null && endTime != null) {
            int startMinutes = startTime.getHour() * 60 + startTime.getMinute();
            int endMinutes = endTime.getHour() * 60 + endTime.getMinute();
            this.durationMinutes = endMinutes - startMinutes;
        }
    }

    public void calculateAmount() {
        if (hourlyRate != null && durationMinutes != null) {
            BigDecimal hours = BigDecimal.valueOf(durationMinutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
            this.amount = hourlyRate.multiply(hours);
        }
    }

    public void stop() {
        this.endTime = LocalTime.now();
        calculateDuration();
        calculateAmount();
    }
}
