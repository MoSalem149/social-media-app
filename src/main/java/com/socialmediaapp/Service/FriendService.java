package com.socialmediaapp.Service;

import com.socialmediaapp.DAO.FriendDAO;
import com.socialmediaapp.DAO.UserDAO;
import com.socialmediaapp.Enum.Status;
import com.socialmediaapp.Enum.Type;
import com.socialmediaapp.Model.Friend;
import com.socialmediaapp.Util.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class FriendService {
    private final FriendDAO friendDAO;
    private final UserDAO userDAO;
    private final AuthService authService;
    private final NotificationService notificationService;

    public FriendService(FriendDAO friendDAO, UserDAO userDAO, AuthService authService,
                         NotificationService notificationService) {
        this.friendDAO = friendDAO;
        this.userDAO = userDAO;
        this.authService = authService;
        this.notificationService = notificationService;
    }

    public Friend getFriendById(int id) {
        return friendDAO.findById(id).orElseThrow(() -> new IllegalArgumentException("Friend Not Found !"));
    }

    public Friend getFriendByFriendId(int friend_id) {
        return friendDAO.findByFriendId(friend_id).orElseThrow(() -> new IllegalArgumentException("Friend NOt Found!"));
    }

    public List<Friend> getAllFriends() {
        return friendDAO.findAll();
    }

    public Page<Friend> getAllFriendsAsPage(int pageNumber, int pageSize, String sortBy, String sortDir,
                                            int userId, int postId) {
        return friendDAO.findAll(pageNumber, pageSize, sortBy, sortDir, Optional.empty(), Optional.of(userId), Optional.empty());
    }

    public List<Friend> getAllFriendsByUserId(int id) {
        return friendDAO.findAllByUserId(id);
    }

    public boolean updateFriend(Friend friend) {
        if (authService.getCurrentUser().getId() == friend.getUserId() || authService.getCurrentUser().getId() == friend.getFriendId()) {
            Optional<Friend> existing = friendDAO.findById(friend.getId());
            if (existing.isPresent()) {
                Status previousStatus = existing.get().getStatus();
                boolean updated = friendDAO.update(friend);
                if (updated && friend.getStatus() == Status.ACCEPTED && previousStatus != Status.ACCEPTED) {
                    int recipientId = (authService.getCurrentUser().getId() == friend.getUserId()) ? friend.getFriendId() : friend.getUserId();
                    notificationService.createNotification(recipientId, authService.getCurrentUser().getId(), Type.FRIEND_ACCEPT, friend.getId());
                }
                return updated;
            } else throw new IllegalArgumentException("Friend Not Found !");
        }
        throw new IllegalArgumentException("Unauthorized !");
    }

    public boolean deleteFriend(Friend friend) {
        if (authService.getCurrentUser().getId() == friend.getUserId() || authService.getCurrentUser().getId() == friend.getFriendId()) {
            if (friendDAO.findById(friend.getId()).isPresent()) {
                return friendDAO.delete(friend);
            } else throw new IllegalArgumentException("Friend Not Found !");
        }
        throw new IllegalArgumentException("Unauthorized !");
    }

    public boolean createFriend(Friend friend) {
        if (friend.getFriendId() == friend.getUserId() || friend.getFriendId() == authService.getCurrentUser().getId()) {
            throw new IllegalArgumentException("You Can't Create Friendship with yourself !");
        }
        if (userDAO.findById(friend.getUserId()).isEmpty() || userDAO.findById(friend.getFriendId()).isEmpty()) {
            throw new IllegalArgumentException("User Not Found !");
        }
        int actualUserId = Math.min(friend.getUserId(), friend.getFriendId());
        int actualFriendId = Math.max(friend.getUserId(), friend.getFriendId());
        if (friendDAO.findByUserIdAndFriendId(actualUserId, actualFriendId).isPresent()) {
            throw new IllegalArgumentException("This Friendship Already Exists!");
        }
        friend.setUserId(actualUserId);
        friend.setFriendId(actualFriendId);
        friend.setStatus(Status.PENDING);
        friend.setCreatedAt(LocalDateTime.now());
        boolean saved = friendDAO.save(friend);
        if (saved) {
            int recipientId = (authService.getCurrentUser().getId() == actualUserId) ? actualFriendId : actualUserId;
            notificationService.createNotification(recipientId, authService.getCurrentUser().getId(), Type.FRIEND_REQUEST, friend.getId());
        }
        return saved;
    }
}