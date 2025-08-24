package com.proximaai.repository;

import com.proximaai.domain.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectId(Long projectId);
    
    List<Task> findByAssigneeId(Long assigneeId);
    
    List<Task> findByReporterId(Long reporterId);
    
    List<Task> findByStatus(Task.TaskStatus status);
    
    List<Task> findByPriority(Task.TaskPriority priority);
    
    List<Task> findByType(Task.TaskType type);
    
    List<Task> findByProjectIdAndStatus(Long projectId, Task.TaskStatus status);
    
    List<Task> findByAssigneeIdAndStatus(Long assigneeId, Task.TaskStatus status);
    
    List<Task> findByProjectIdAndAssigneeId(Long projectId, Long assigneeId);
    
    // Kanban Board queries
    List<Task> findByProjectIdAndKanbanColumnOrderByKanbanOrderAsc(Long projectId, String kanbanColumn);
    
    List<Task> findByProjectIdAndKanbanColumnInOrderByKanbanOrderAsc(Long projectId, List<String> kanbanColumns);
    
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId ORDER BY t.kanbanOrder ASC")
    List<Task> findAllByProjectOrderByKanbanOrder(@Param("projectId") Long projectId);
    
    // Overdue tasks
    List<Task> findByDueDateBeforeAndStatusNot(LocalDate dueDate, Task.TaskStatus status);
    
    List<Task> findByAssigneeIdAndDueDateBeforeAndStatusNot(Long assigneeId, LocalDate dueDate, Task.TaskStatus status);
    
    // Due date queries
    List<Task> findByDueDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<Task> findByDueDate(LocalDate dueDate);
    
    // Search queries
    @Query("SELECT t FROM Task t WHERE " +
           "LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Task> searchTasks(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND " +
           "(LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Task> searchTasksInProject(@Param("projectId") Long projectId, 
                                   @Param("searchTerm") String searchTerm, 
                                   Pageable pageable);
    
    // Priority and status combinations
    List<Task> findByPriorityAndStatus(Task.TaskPriority priority, Task.TaskStatus status);
    
    List<Task> findByPriorityInAndStatusIn(List<Task.TaskPriority> priorities, List<Task.TaskStatus> statuses);
    
    // Story points queries
    List<Task> findByStoryPointsBetween(Integer minPoints, Integer maxPoints);
    
    List<Task> findByStoryPointsGreaterThan(Integer points);
    
    // Time tracking queries
    @Query("SELECT t FROM Task t WHERE t.estimatedHours IS NOT NULL AND t.actualHours IS NOT NULL " +
           "AND t.actualHours > t.estimatedHours")
    List<Task> findOverestimatedTasks();
    
    @Query("SELECT t FROM Task t WHERE t.estimatedHours IS NOT NULL AND t.actualHours IS NOT NULL " +
           "AND t.actualHours < t.estimatedHours")
    List<Task> findUnderestimatedTasks();
    
    // AI recommendations queries
    List<Task> findByAiPriorityScoreGreaterThan(Double minScore);
    
    List<Task> findByAiEstimatedDurationIsNotNull();
    
    // Statistics queries
    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId AND t.status = :status")
    long countByProjectAndStatus(@Param("projectId") Long projectId, @Param("status") Task.TaskStatus status);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignee.id = :assigneeId AND t.status = :status")
    long countByAssigneeAndStatus(@Param("assigneeId") Long assigneeId, @Param("status") Task.TaskStatus status);
    
    @Query("SELECT AVG(t.storyPoints) FROM Task t WHERE t.project.id = :projectId AND t.storyPoints IS NOT NULL")
    Double getAverageStoryPointsByProject(@Param("projectId") Long projectId);
    
    @Query("SELECT SUM(t.actualHours) FROM Task t WHERE t.project.id = :projectId AND t.actualHours IS NOT NULL")
    Double getTotalActualHoursByProject(@Param("projectId") Long projectId);
    
    // Recent tasks
    List<Task> findTop10ByOrderByCreatedAtDesc();
    
    List<Task> findTop10ByAssigneeIdOrderByCreatedAtDesc(Long assigneeId);
    
    List<Task> findTop10ByProjectIdOrderByCreatedAtDesc(Long projectId);
    
    // Tasks by date range
    @Query("SELECT t FROM Task t WHERE t.createdAt BETWEEN :startDate AND :endDate")
    List<Task> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT t FROM Task t WHERE t.startDate BETWEEN :startDate AND :endDate")
    List<Task> findByStartDateBetween(@Param("startDate") LocalDate startDate, 
                                     @Param("endDate") LocalDate endDate);
    
    @Query("SELECT t FROM Task t WHERE t.completedDate BETWEEN :startDate AND :endDate")
    List<Task> findByCompletedDateBetween(@Param("startDate") LocalDate startDate, 
                                         @Param("endDate") LocalDate endDate);
    
    // Tasks with specific tags
    @Query("SELECT t FROM Task t WHERE t.tags LIKE CONCAT('%', :tag, '%')")
    List<Task> findByTag(@Param("tag") String tag);
    
    // Tasks by multiple criteria
    @Query("SELECT t FROM Task t WHERE " +
           "(:projectId IS NULL OR t.project.id = :projectId) AND " +
           "(:assigneeId IS NULL OR t.assignee.id = :assigneeId) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:priority IS NULL OR t.priority = :priority) AND " +
           "(:type IS NULL OR t.type = :type)")
    Page<Task> findByMultipleCriteria(@Param("projectId") Long projectId,
                                     @Param("assigneeId") Long assigneeId,
                                     @Param("status") Task.TaskStatus status,
                                     @Param("priority") Task.TaskPriority priority,
                                     @Param("type") Task.TaskType type,
                                     Pageable pageable);
}
