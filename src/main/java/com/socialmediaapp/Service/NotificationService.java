package com.socialmediaapp.Service;

import com.socialmediaapp.DAO.NotificationDAO;
import com.socialmediaapp.Model.Notification;
import java.util.List;

public class NotificationService {
    private final NotificationDAO notificationDAO;
    private final AuthService authService;

    public NotificationService(NotificationDAO notificationDAO, AuthService authService) {
        this.notificationDAO = notificationDAO;
        this.authService = authService;
    }

    public List<Notification> getNotificationsForCurrentUser() {
        return notificationDAO.findAllByUserId(authService.getCurrentUser().getId());
    }

    public void markAsRead(Notification notification) {
        notification.setRead(true);
        notificationDAO.update(notification);
    }

    public void markAllAsRead() {
        notificationDAO.markAllAsRead(authService.getCurrentUser().getId());
    }

    // Additional methods for creating notifications (e.g., when someone likes or comments) can be added.
}