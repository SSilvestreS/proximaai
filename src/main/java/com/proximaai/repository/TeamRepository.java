package com.proximaai.repository;

import com.proximaai.domain.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    /**
     * Busca equipes por status
     */
    List<Team> findByStatus(Team.TeamStatus status);

    /**
     * Busca equipes por nome (case insensitive)
     */
    List<Team> findByNameContainingIgnoreCase(String name);

    /**
     * Busca equipes ativas
     */
    @Query("SELECT t FROM Team t WHERE t.status = 'ACTIVE'")
    List<Team> findActiveTeams();

    /**
     * Busca equipes por líder
     */
    @Query("SELECT t FROM Team t WHERE t.leader.id = :leaderId")
    List<Team> findByLeaderId(@Param("leaderId") Long leaderId);

    /**
     * Busca equipes por membro
     */
    @Query("SELECT t FROM Team t JOIN t.members m WHERE m.id = :memberId")
    List<Team> findByMemberId(@Param("memberId") Long memberId);

    /**
     * Conta equipes por status
     */
    long countByStatus(Team.TeamStatus status);

    /**
     * Busca equipes por nome exato
     */
    Optional<Team> findByName(String name);

    /**
     * Verifica se existe equipe com nome
     */
    boolean existsByName(String name);

    /**
     * Busca equipes por descrição
     */
    List<Team> findByDescriptionContainingIgnoreCase(String description);

    /**
     * Busca equipes criadas por um usuário
     */
    List<Team> findByCreatedBy(String createdBy);

    /**
     * Busca equipes atualizadas recentemente
     */
    @Query("SELECT t FROM Team t WHERE t.updatedAt >= :since ORDER BY t.updatedAt DESC")
    List<Team> findRecentlyUpdatedTeams(@Param("since") java.time.LocalDateTime since);

    /**
     * Busca equipes por múltiplos status
     */
    @Query("SELECT t FROM Team t WHERE t.status IN :statuses")
    List<Team> findByStatusIn(@Param("statuses") List<Team.TeamStatus> statuses);

    /**
     * Busca equipes com número mínimo de membros
     */
    @Query("SELECT t FROM Team t WHERE SIZE(t.members) >= :minMembers")
    List<Team> findTeamsWithMinimumMembers(@Param("minMembers") int minMembers);

    /**
     * Busca equipes com número máximo de membros
     */
    @Query("SELECT t FROM Team t WHERE SIZE(t.members) <= :maxMembers")
    List<Team> findTeamsWithMaximumMembers(@Param("maxMembers") int maxMembers);

    /**
     * Busca equipes por faixa de número de membros
     */
    @Query("SELECT t FROM Team t WHERE SIZE(t.members) BETWEEN :minMembers AND :maxMembers")
    List<Team> findTeamsByMemberCountRange(@Param("minMembers") int minMembers, @Param("maxMembers") int maxMembers);

    /**
     * Busca equipes que não têm líder
     */
    @Query("SELECT t FROM Team t WHERE t.leader IS NULL")
    List<Team> findTeamsWithoutLeader();

    /**
     * Busca equipes que têm projetos ativos
     */
    @Query("SELECT DISTINCT t FROM Team t JOIN t.projects p WHERE p.status IN ('ACTIVE', 'IN_PROGRESS')")
    List<Team> findTeamsWithActiveProjects();

    /**
     * Conta número de membros por equipe
     */
    @Query("SELECT t.id, SIZE(t.members) FROM Team t")
    List<Object[]> countMembersByTeam();

    /**
     * Busca equipes por departamento
     */
    @Query("SELECT t FROM Team t WHERE t.department = :department")
    List<Team> findByDepartment(@Param("department") String department);

    /**
     * Busca equipes por localização
     */
    @Query("SELECT t FROM Team t WHERE t.location = :location")
    List<Team> findByLocation(@Param("location") String location);
}
