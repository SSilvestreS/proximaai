package com.proximaai.repository;

import com.proximaai.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByUsernameOrEmail(String username, String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    List<User> findByRole(User.UserRole role);
    
    List<User> findByStatus(User.UserStatus status);
    
    List<User> findByTeamId(Long teamId);
    
    @Query("SELECT u FROM User u WHERE u.lastLogin < :date")
    List<User> findInactiveUsers(@Param("date") LocalDateTime date);
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.status = :status")
    List<User> findByRoleAndStatus(@Param("role") User.UserRole role, 
                                  @Param("status") User.UserStatus status);
    
    @Query("SELECT u FROM User u JOIN u.teams t WHERE t.id = :teamId")
    List<User> findUsersByTeamId(@Param("teamId") Long teamId);
    
    @Query("SELECT u FROM User u WHERE u.id IN " +
           "(SELECT DISTINCT t.assignee.id FROM Task t WHERE t.project.id = :projectId)")
    List<User> findUsersByProjectId(@Param("projectId") Long projectId);
    
    @Query("SELECT u FROM User u WHERE u.id IN " +
           "(SELECT DISTINCT t.assignee.id FROM Task t WHERE t.status = 'IN_PROGRESS')")
    List<User> findUsersWithActiveTasks();
    
    @Query("SELECT u FROM User u WHERE u.id IN " +
           "(SELECT DISTINCT t.assignee.id FROM Task t WHERE t.dueDate < :date AND t.status != 'DONE')")
    List<User> findUsersWithOverdueTasks(@Param("date") LocalDateTime date);
    
    @Query("SELECT u FROM User u WHERE " +
           "(SELECT COUNT(t) FROM Task t WHERE t.assignee = u AND t.status = 'IN_PROGRESS') > :maxTasks")
    List<User> findOverloadedUsers(@Param("maxTasks") int maxTasks);
    
    Page<User> findByUsernameContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
        String username, String firstName, String lastName, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<User> searchUsers(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.createdAt >= :startDate AND u.createdAt <= :endDate")
    List<User> findUsersCreatedBetween(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT u FROM User u WHERE u.lastLogin IS NULL OR u.lastLogin < :date")
    List<User> findUsersNotLoggedInSince(@Param("date") LocalDateTime date);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = 'ACTIVE'")
    long countActiveUsers();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countUsersByRole(@Param("role") User.UserRole role);
}
