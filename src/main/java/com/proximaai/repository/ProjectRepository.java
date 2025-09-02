package com.proximaai.repository;

import com.proximaai.domain.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    /**
     * Busca projetos por status
     */
    List<Project> findByStatus(Project.ProjectStatus status);

    /**
     * Busca projetos por prioridade
     */
    List<Project> findByPriority(Project.ProjectPriority priority);

    /**
     * Busca projetos por equipe
     */
    List<Project> findByTeamId(Long teamId);

    /**
     * Busca projetos por nome (case insensitive)
     */
    List<Project> findByNameContainingIgnoreCase(String name);

    /**
     * Busca projetos ativos
     */
    @Query("SELECT p FROM Project p WHERE p.status IN ('ACTIVE', 'IN_PROGRESS')")
    List<Project> findActiveProjects();

    /**
     * Busca projetos que terminam em uma data específica
     */
    List<Project> findByEndDate(LocalDate endDate);

    /**
     * Busca projetos que terminam antes de uma data
     */
    List<Project> findByEndDateBefore(LocalDate date);

    /**
     * Busca projetos que terminam depois de uma data
     */
    List<Project> findByEndDateAfter(LocalDate date);

    /**
     * Busca projetos por período
     */
    @Query("SELECT p FROM Project p WHERE p.startDate <= :endDate AND p.endDate >= :startDate")
    List<Project> findProjectsByPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Conta projetos por status
     */
    long countByStatus(Project.ProjectStatus status);

    /**
     * Conta projetos por equipe
     */
    long countByTeamId(Long teamId);

    /**
     * Busca projetos com tarefas atrasadas
     */
    @Query("SELECT DISTINCT p FROM Project p JOIN p.tasks t WHERE t.dueDate < CURRENT_DATE AND t.status != 'DONE'")
    List<Project> findProjectsWithOverdueTasks();

    /**
     * Busca projetos por usuário (através de tarefas)
     */
    @Query("SELECT DISTINCT p FROM Project p JOIN p.tasks t WHERE t.assignee.id = :userId")
    List<Project> findProjectsByUserId(@Param("userId") Long userId);

    /**
     * Busca projetos críticos
     */
    @Query("SELECT p FROM Project p WHERE p.priority = 'CRITICAL' AND p.status IN ('ACTIVE', 'IN_PROGRESS')")
    List<Project> findCriticalProjects();

    /**
     * Busca projetos próximos do prazo
     */
    @Query("SELECT p FROM Project p WHERE p.endDate <= :deadline AND p.status IN ('ACTIVE', 'IN_PROGRESS')")
    List<Project> findProjectsNearDeadline(@Param("deadline") LocalDate deadline);

    /**
     * Busca projetos por descrição
     */
    List<Project> findByDescriptionContainingIgnoreCase(String description);

    /**
     * Busca projetos criados por um usuário
     */
    List<Project> findByCreatedBy(String createdBy);

    /**
     * Busca projetos atualizados recentemente
     */
    @Query("SELECT p FROM Project p WHERE p.updatedAt >= :since ORDER BY p.updatedAt DESC")
    List<Project> findRecentlyUpdatedProjects(@Param("since") java.time.LocalDateTime since);

    /**
     * Busca projetos por nome exato
     */
    Optional<Project> findByName(String name);

    /**
     * Verifica se existe projeto com nome
     */
    boolean existsByName(String name);

    /**
     * Busca projetos por múltiplos status
     */
    @Query("SELECT p FROM Project p WHERE p.status IN :statuses")
    List<Project> findByStatusIn(@Param("statuses") List<Project.ProjectStatus> statuses);

    /**
     * Busca projetos por múltiplas prioridades
     */
    @Query("SELECT p FROM Project p WHERE p.priority IN :priorities")
    List<Project> findByPriorityIn(@Param("priorities") List<Project.ProjectPriority> priorities);
}
