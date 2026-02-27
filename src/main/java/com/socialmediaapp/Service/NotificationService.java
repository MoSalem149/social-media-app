package com.socialmediaapp.Service;

import com.socialmediaapp.DAO.NotificationDAO;
import com.socialmediaapp.Enum.Type;
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

    public long getUnreadCount() {
        return getNotificationsForCurrentUser().stream().filter(n -> !n.isRead()).count();
    }

    public boolean markAsRead(Notification notification) {
        if (notification.getUserId() != authService.getCurrentUser().getId()) {
            return false;
        }
        notification.setRead(true);
        return notificationDAO.update(notification);
    }

    public boolean markAllAsRead() {
        return notificationDAO.markAllAsRead(authService.getCurrentUser().getId());
    }

    /**
     * Create a notification (used by LikeService, CommentService, FriendService).
     * recipientUserId: user who receives the notification.
     * senderId: user who triggered it (liker, commenter, requester, accepter).
     */
    public boolean createNotification(int recipientUserId, int senderId, Type type, int referenceId) {
        if (recipientUserId == senderId) {
            return false; // don't notify self
        }
        Notification n = Notification.builder()
                .userId(recipientUserId)
                .senderId(senderId)
                .type(type)
                .referenceId(referenceId)
                .read(false)
                .build();
        return notificationDAO.save(n);
    }
}
