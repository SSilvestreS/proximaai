package com.proximaai.service.ai;

import com.proximaai.domain.entity.ai.AllocationRecommendation;
import com.proximaai.domain.entity.Task;
import com.proximaai.domain.entity.User;
import com.proximaai.domain.entity.Project;
import com.proximaai.repository.ai.AllocationRecommendationRepository;

import com.proximaai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AllocationRecommendationService {

    @Autowired
    private AllocationRecommendationRepository allocationRecommendationRepository;



    @Autowired
    private UserRepository userRepository;

    /**
     * Gera recomendações de alocação para uma tarefa
     */
    public List<AllocationRecommendation> generateRecommendationsForTask(Task task) {
        // Busca usuários disponíveis para o projeto
        List<User> availableUsers = getAvailableUsersForProject(task.getProject());
        
        // Gera recomendações para cada usuário
        List<AllocationRecommendation> recommendations = availableUsers.stream()
                .map(user -> createRecommendation(task, user))
                .sorted((r1, r2) -> r2.getRecommendationScore().compareTo(r1.getRecommendationScore()))
                .collect(Collectors.toList());

        // Salva as recomendações
        return allocationRecommendationRepository.saveAll(recommendations);
    }

    /**
     * Cria uma recomendação específica para tarefa e usuário
     */
    private AllocationRecommendation createRecommendation(Task task, User user) {
        // Calcula scores individuais
        BigDecimal skillMatchScore = calculateSkillMatchScore(task, user);
        BigDecimal availabilityScore = calculateAvailabilityScore(user);
        BigDecimal workloadScore = calculateWorkloadScore(user);
        BigDecimal experienceScore = calculateExperienceScore(user, task);

        // Calcula score geral ponderado
        BigDecimal recommendationScore = calculateOverallScore(
            skillMatchScore, availabilityScore, workloadScore, experienceScore
        );

        // Determina nível de confiança
        AllocationRecommendation.ConfidenceLevel confidenceLevel = determineConfidenceLevel(recommendationScore);

        // Cria a recomendação
        AllocationRecommendation recommendation = new AllocationRecommendation();
        recommendation.setTask(task);
        recommendation.setRecommendedUser(user);
        recommendation.setRecommendationScore(recommendationScore);
        recommendation.setSkillMatchPercentage(skillMatchScore.multiply(BigDecimal.valueOf(100)));
        recommendation.setAvailabilityScore(availabilityScore);
        recommendation.setWorkloadScore(workloadScore);
        recommendation.setExperienceScore(experienceScore);
        recommendation.setConfidenceLevel(confidenceLevel);
        recommendation.setIsImplemented(false);

        return recommendation;
    }

    /**
     * Calcula score de match de skills
     */
    private BigDecimal calculateSkillMatchScore(Task task, User user) {
        BigDecimal score = BigDecimal.ZERO;
        
        // Implementação simplificada - em produção seria baseada em skills reais
        // Aqui simulamos um score baseado em experiência e tipo de tarefa
        
        // Score baseado em anos de experiência (simulado)
        Integer experienceYears = getSimulatedExperienceYears(user);
        if (experienceYears != null) {
            if (experienceYears >= 5) {
                score = score.add(BigDecimal.valueOf(0.4));
            } else if (experienceYears >= 3) {
                score = score.add(BigDecimal.valueOf(0.3));
            } else if (experienceYears >= 1) {
                score = score.add(BigDecimal.valueOf(0.2));
            }
        }

        // Score baseado no tipo de tarefa
        if (task.getType() != null) {
            switch (task.getType()) {
                case TASK -> score = score.add(BigDecimal.valueOf(0.3));
                case BUG -> score = score.add(BigDecimal.valueOf(0.2));
                case FEATURE -> score = score.add(BigDecimal.valueOf(0.25));
                case STORY -> score = score.add(BigDecimal.valueOf(0.15));
                default -> score = score.add(BigDecimal.valueOf(0.1));
            }
        }

        return score.min(BigDecimal.ONE);
    }

    /**
     * Calcula score de disponibilidade
     */
    private BigDecimal calculateAvailabilityScore(User user) {
        // Implementação simplificada - em produção seria baseada em calendário real
        BigDecimal baseAvailability = BigDecimal.valueOf(0.8);
        
        // Ajusta baseado em workload atual
        BigDecimal currentWorkload = getUserCurrentWorkload(user);
        if (currentWorkload.compareTo(BigDecimal.valueOf(0.7)) > 0) {
            baseAvailability = baseAvailability.subtract(BigDecimal.valueOf(0.2));
        } else if (currentWorkload.compareTo(BigDecimal.valueOf(0.5)) > 0) {
            baseAvailability = baseAvailability.subtract(BigDecimal.valueOf(0.1));
        }

        return baseAvailability.max(BigDecimal.ZERO);
    }

    /**
     * Calcula score de workload (menor = melhor)
     */
    private BigDecimal calculateWorkloadScore(User user) {
        BigDecimal workload = getUserCurrentWorkload(user);
        
        // Inverte o score para que menor workload = maior score
        return BigDecimal.ONE.subtract(workload);
    }

    /**
     * Calcula score de experiência específica
     */
    private BigDecimal calculateExperienceScore(User user, Task task) {
        BigDecimal score = BigDecimal.ZERO;
        
        // Score baseado em anos de experiência (simulado)
        Integer experienceYears = getSimulatedExperienceYears(user);
        if (experienceYears != null) {
            if (experienceYears >= 8) {
                score = score.add(BigDecimal.valueOf(0.4));
            } else if (experienceYears >= 5) {
                score = score.add(BigDecimal.valueOf(0.3));
            } else if (experienceYears >= 2) {
                score = score.add(BigDecimal.valueOf(0.2));
            } else {
                score = score.add(BigDecimal.valueOf(0.1));
            }
        }

        // Score baseado em histórico de tarefas similares (simulado)
        score = score.add(BigDecimal.valueOf(0.2));

        return score.min(BigDecimal.ONE);
    }

    /**
     * Calcula score geral ponderado
     */
    private BigDecimal calculateOverallScore(
            BigDecimal skillMatch,
            BigDecimal availability,
            BigDecimal workload,
            BigDecimal experience) {
        
        return skillMatch.multiply(BigDecimal.valueOf(0.35))
                .add(availability.multiply(BigDecimal.valueOf(0.25)))
                .add(workload.multiply(BigDecimal.valueOf(0.20)))
                .add(experience.multiply(BigDecimal.valueOf(0.20)));
    }

    /**
     * Determina nível de confiança baseado no score
     */
    private AllocationRecommendation.ConfidenceLevel determineConfidenceLevel(BigDecimal score) {
        if (score.compareTo(BigDecimal.valueOf(0.8)) >= 0) {
            return AllocationRecommendation.ConfidenceLevel.HIGH;
        } else if (score.compareTo(BigDecimal.valueOf(0.6)) >= 0) {
            return AllocationRecommendation.ConfidenceLevel.MEDIUM;
        } else {
            return AllocationRecommendation.ConfidenceLevel.LOW;
        }
    }

    /**
     * Obtém workload atual do usuário
     */
    private BigDecimal getUserCurrentWorkload(User user) {
        // Implementação simplificada - em produção seria baseada em tarefas ativas
        return BigDecimal.valueOf(0.6); // Workload médio padrão
    }

    /**
     * Busca usuários disponíveis para o projeto
     */
    private List<User> getAvailableUsersForProject(Project project) {
        // Implementação simplificada - em produção seria baseada em equipe e disponibilidade
        return userRepository.findAll().stream()
                .limit(5) // Limita a 5 usuários para demonstração
                .collect(Collectors.toList());
    }

    /**
     * Busca recomendações por tarefa
     */
    public List<AllocationRecommendation> getRecommendationsForTask(Task task) {
        return allocationRecommendationRepository.findByTaskOrderByRecommendationScoreDesc(task);
    }

    /**
     * Busca a melhor recomendação para uma tarefa
     */
    public Optional<AllocationRecommendation> getBestRecommendationForTask(Task task) {
        return allocationRecommendationRepository.findFirstByTaskOrderByRecommendationScoreDesc(task);
    }

    /**
     * Busca recomendações por usuário
     */
    public List<AllocationRecommendation> getRecommendationsForUser(User user) {
        return allocationRecommendationRepository.findByRecommendedUserOrderByRecommendationScoreDesc(user);
    }

    /**
     * Busca recomendações com alta confiança
     */
    public List<AllocationRecommendation> getHighConfidenceRecommendations() {
        return allocationRecommendationRepository.findByConfidenceLevel(AllocationRecommendation.ConfidenceLevel.HIGH);
    }

    /**
     * Marca recomendação como implementada
     */
    public void markRecommendationAsImplemented(Long recommendationId) {
        Optional<AllocationRecommendation> recommendationOpt = 
            allocationRecommendationRepository.findById(recommendationId);
        
        if (recommendationOpt.isPresent()) {
            AllocationRecommendation recommendation = recommendationOpt.get();
            recommendation.setIsImplemented(true);
            allocationRecommendationRepository.save(recommendation);
        }
    }

    /**
     * Atualiza performance real da recomendação
     */
    public void updateRecommendationPerformance(Long recommendationId, BigDecimal actualPerformanceScore) {
        Optional<AllocationRecommendation> recommendationOpt = 
            allocationRecommendationRepository.findById(recommendationId);
        
        if (recommendationOpt.isPresent()) {
            AllocationRecommendation recommendation = recommendationOpt.get();
            recommendation.setActualPerformanceScore(actualPerformanceScore);
            allocationRecommendationRepository.save(recommendation);
        }
    }

    /**
     * Busca recomendações alternativas excluindo um usuário
     */
    public List<AllocationRecommendation> getAlternativeRecommendations(Task task, User excludeUser) {
        return allocationRecommendationRepository.findAlternativesForTask(task, excludeUser);
    }

    /**
     * Recalcula recomendações para uma tarefa
     */
    public void recalculateRecommendationsForTask(Task task) {
        // Remove recomendações antigas
        List<AllocationRecommendation> oldRecommendations = 
            allocationRecommendationRepository.findByTaskOrderByRecommendationScoreDesc(task);
        
        if (!oldRecommendations.isEmpty()) {
            allocationRecommendationRepository.deleteAll(oldRecommendations);
        }

        // Gera novas recomendações
        generateRecommendationsForTask(task);
    }

    /**
     * Calcula métricas de qualidade das recomendações
     */
    public BigDecimal calculateRecommendationQuality() {
        List<AllocationRecommendation> implementedRecommendations = 
            allocationRecommendationRepository.findImplementedWithPerformance();
        
        if (implementedRecommendations.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalQuality = implementedRecommendations.stream()
                .map(AllocationRecommendation::getActualPerformanceScore)
                .filter(score -> score != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalQuality.divide(
            BigDecimal.valueOf(implementedRecommendations.size()), 
            4, 
            RoundingMode.HALF_UP
        );
    }

    /**
     * Simula anos de experiência baseado no ID do usuário
     */
    private Integer getSimulatedExperienceYears(User user) {
        // Implementação simplificada - em produção seria um campo real
        if (user.getId() != null) {
            return (user.getId().intValue() % 10) + 1; // 1-10 anos
        }
        return 3; // Padrão
    }
}
