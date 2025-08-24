package com.proximaai.service;

import com.proximaai.domain.entity.Notification;
import com.proximaai.domain.entity.User;
import com.proximaai.domain.entity.Task;
import com.proximaai.domain.entity.Project;
import com.proximaai.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Envia notificação em tempo real via WebSocket
     */
    public void sendRealTimeNotification(Notification notification) {
        // Salva a notificação no banco
        Notification savedNotification = notificationRepository.save(notification);
        
        // Envia via WebSocket para o usuário específico
        String destination = "/user/" + notification.getRecipient().getId() + "/notifications";
        messagingTemplate.convertAndSendToUser(
            notification.getRecipient().getId().toString(),
            destination,
            savedNotification
        );
        
        // Envia para o tópico geral se for notificação pública
        if (notification.getType() == Notification.NotificationType.INFO) {
            messagingTemplate.convertAndSend("/topic/notifications", savedNotification);
        }
    }

    /**
     * Cria e envia notificação de tarefa atribuída
     */
    public void notifyTaskAssigned(Task task, User assignee) {
        Notification notification = new Notification(
            "Tarefa Atribuída",
            "A tarefa '" + task.getTitle() + "' foi atribuída para você",
            assignee,
            Notification.NotificationType.TASK_ASSIGNED
        );
        
        notification.setEntityType("TASK");
        notification.setEntityId(task.getId());
        notification.setActionUrl("/tasks/" + task.getId());
        notification.setPriority(Notification.NotificationPriority.NORMAL);
        
        sendRealTimeNotification(notification);
    }

    /**
     * Cria e envia notificação de tarefa concluída
     */
    public void notifyTaskCompleted(Task task, User assignee) {
        Notification notification = new Notification(
            "Tarefa Concluída",
            "A tarefa '" + task.getTitle() + "' foi marcada como concluída",
            assignee,
            Notification.NotificationType.TASK_COMPLETED
        );
        
        notification.setEntityType("TASK");
        notification.setEntityId(task.getId());
        notification.setActionUrl("/tasks/" + task.getId());
        notification.setPriority(Notification.NotificationPriority.NORMAL);
        
        sendRealTimeNotification(notification);
    }

    /**
     * Cria e envia notificação de prazo se aproximando
     */
    public void notifyDeadlineApproaching(Task task, User assignee) {
        Notification notification = new Notification(
            "Prazo se Aproximando",
            "A tarefa '" + task.getTitle() + "' vence em breve",
            assignee,
            Notification.NotificationType.DEADLINE_APPROACHING
        );
        
        notification.setEntityType("TASK");
        notification.setEntityId(task.getId());
        notification.setActionUrl("/tasks/" + task.getId());
        notification.setPriority(Notification.NotificationPriority.HIGH);
        
        sendRealTimeNotification(notification);
    }

    /**
     * Cria e envia notificação de tarefa atrasada
     */
    public void notifyOverdueTask(Task task, User assignee) {
        Notification notification = new Notification(
            "Tarefa Atrasada",
            "A tarefa '" + task.getTitle() + "' está atrasada",
            assignee,
            Notification.NotificationType.OVERDUE_TASK
        );
        
        notification.setEntityType("TASK");
        notification.setEntityId(task.getId());
        notification.setActionUrl("/tasks/" + task.getId());
        notification.setPriority(Notification.NotificationPriority.URGENT);
        
        sendRealTimeNotification(notification);
    }

    /**
     * Cria e envia notificação de atualização do projeto
     */
    public void notifyProjectUpdate(Project project, User user, String message) {
        Notification notification = new Notification(
            "Atualização do Projeto",
            message,
            user,
            Notification.NotificationType.PROJECT_UPDATE
        );
        
        notification.setEntityType("PROJECT");
        notification.setEntityId(project.getId());
        notification.setActionUrl("/projects/" + project.getId());
        notification.setPriority(Notification.NotificationPriority.NORMAL);
        
        sendRealTimeNotification(notification);
    }

    /**
     * Cria e envia notificação de convite para equipe
     */
    public void notifyTeamInvitation(String teamName, User invitedUser, User inviter) {
        Notification notification = new Notification(
            "Convite para Equipe",
            "Você foi convidado para participar da equipe '" + teamName + "' por " + inviter.getFullName(),
            invitedUser,
            Notification.NotificationType.TEAM_INVITATION
        );
        
        notification.setEntityType("TEAM");
        notification.setPriority(Notification.NotificationPriority.HIGH);
        notification.setSender(inviter);
        
        sendRealTimeNotification(notification);
    }

    /**
     * Cria e envia notificação de comentário em tarefa
     */
    public void notifyTaskComment(Task task, User commentAuthor, User taskAssignee) {
        if (commentAuthor.equals(taskAssignee)) {
            return; // Não notifica o próprio autor
        }
        
        Notification notification = new Notification(
            "Novo Comentário",
            "Novo comentário na tarefa '" + task.getTitle() + "' por " + commentAuthor.getFullName(),
            taskAssignee,
            Notification.NotificationType.INFO
        );
        
        notification.setEntityType("TASK");
        notification.setEntityId(task.getId());
        notification.setActionUrl("/tasks/" + task.getId());
        notification.setPriority(Notification.NotificationPriority.LOW);
        notification.setSender(commentAuthor);
        
        sendRealTimeNotification(notification);
    }

    /**
     * Cria e envia notificação de mudança de status
     */
    public void notifyStatusChange(Task task, String oldStatus, String newStatus, User assignee) {
        Notification notification = new Notification(
            "Status Alterado",
            "Status da tarefa '" + task.getTitle() + "' alterado de " + oldStatus + " para " + newStatus,
            assignee,
            Notification.NotificationType.INFO
        );
        
        notification.setEntityType("TASK");
        notification.setEntityId(task.getId());
        notification.setActionUrl("/tasks/" + task.getId());
        notification.setPriority(Notification.NotificationPriority.NORMAL);
        
        sendRealTimeNotification(notification);
    }

    /**
     * Cria e envia notificação de mudança de prioridade
     */
    public void notifyPriorityChange(Task task, String oldPriority, String newPriority, User assignee) {
        Notification notification = new Notification(
            "Prioridade Alterada",
            "Prioridade da tarefa '" + task.getTitle() + "' alterada de " + oldPriority + " para " + newPriority,
            assignee,
            Notification.NotificationType.INFO
        );
        
        notification.setEntityType("TASK");
        notification.setEntityId(task.getId());
        notification.setActionUrl("/tasks/" + task.getId());
        notification.setPriority(Notification.NotificationPriority.NORMAL);
        
        sendRealTimeNotification(notification);
    }

    /**
     * Cria e envia notificação de sobrecarga de trabalho
     */
    public void notifyWorkloadOverload(User user) {
        Notification notification = new Notification(
            "Sobrecarga de Trabalho",
            "Você tem muitas tarefas ativas. Considere priorizar ou delegar algumas tarefas.",
            user,
            Notification.NotificationType.WARNING
        );
        
        notification.setPriority(Notification.NotificationPriority.HIGH);
        notification.setActionUrl("/dashboard");
        
        sendRealTimeNotification(notification);
    }

    /**
     * Cria e envia notificação de risco do projeto
     */
    public void notifyProjectRisk(Project project, String riskType, String description, User owner) {
        Notification notification = new Notification(
            "Risco Identificado",
            "Risco identificado no projeto '" + project.getName() + "': " + description,
            owner,
            Notification.NotificationType.WARNING
        );
        
        notification.setEntityType("PROJECT");
        notification.setEntityId(project.getId());
        notification.setActionUrl("/projects/" + project.getId() + "/risks");
        notification.setPriority(Notification.NotificationPriority.HIGH);
        
        sendRealTimeNotification(notification);
    }

    /**
     * Marca notificação como lida
     */
    public void markNotificationAsRead(Long notificationId, Long userId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            
            // Verifica se o usuário é o destinatário
            if (notification.getRecipient().getId().equals(userId)) {
                notification.markAsRead();
                notificationRepository.save(notification);
            }
        }
    }

    /**
     * Marca todas as notificações do usuário como lidas
     */
    public void markAllNotificationsAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository.findByRecipientIdAndStatus(
            userId, Notification.NotificationStatus.UNREAD);
        
        for (Notification notification : unreadNotifications) {
            notification.markAsRead();
        }
        
        notificationRepository.saveAll(unreadNotifications);
    }

    /**
     * Busca notificações não lidas do usuário
     */
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByRecipientIdAndStatus(
            userId, Notification.NotificationStatus.UNREAD);
    }

    /**
     * Busca todas as notificações do usuário
     */
    public List<Notification> getUserNotifications(Long userId, int page, int size) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId, page, size);
    }

    /**
     * Remove notificação
     */
    public void deleteNotification(Long notificationId, Long userId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            
            // Verifica se o usuário é o destinatário
            if (notification.getRecipient().getId().equals(userId)) {
                notificationRepository.delete(notification);
            }
        }
    }

    /**
     * Agenda notificação para envio futuro
     */
    public void scheduleNotification(Notification notification, LocalDateTime scheduledFor) {
        notification.setScheduledFor(scheduledFor);
        notificationRepository.save(notification);
    }

    /**
     * Envia notificação para múltiplos usuários
     */
    public void sendBulkNotification(String title, String message, List<User> recipients, 
                                   Notification.NotificationType type, String entityType, Long entityId) {
        for (User recipient : recipients) {
            Notification notification = new Notification(title, message, recipient, type);
            notification.setEntityType(entityType);
            notification.setEntityId(entityId);
            
            sendRealTimeNotification(notification);
        }
    }

    /**
     * Envia notificação de sistema
     */
    public void sendSystemNotification(String title, String message, User recipient) {
        Notification notification = new Notification(title, message, recipient, Notification.NotificationType.INFO);
        notification.setPriority(Notification.NotificationPriority.NORMAL);
        
        sendRealTimeNotification(notification);
    }

    /**
     * Envia notificação de erro
     */
    public void sendErrorNotification(String title, String message, User recipient) {
        Notification notification = new Notification(title, message, recipient, Notification.NotificationType.ERROR);
        notification.setPriority(Notification.NotificationPriority.HIGH);
        
        sendRealTimeNotification(notification);
    }

    /**
     * Envia notificação de sucesso
     */
    public void sendSuccessNotification(String title, String message, User recipient) {
        Notification notification = new Notification(title, message, recipient, Notification.NotificationType.SUCCESS);
        notification.setPriority(Notification.NotificationPriority.NORMAL);
        
        sendRealTimeNotification(notification);
    }
}
