package com.socialmediaapp.Util;

import com.socialmediaapp.DAO.*;
import com.socialmediaapp.Service.*;

/**
 * Central place to obtain all services for the JavaFX application.
 * Initialized once at startup.
 */
public final class AppContext {
    private static AuthService authService;
    private static UserService userService;
    private static PostService postService;
    private static CommentService commentService;
    private static LikeService likeService;
    private static FriendService friendService;
    private static NotificationService notificationService;

    public static void init() {
        UserDAO userDAO = new UserDAO();
        PostDAO postDAO = new PostDAO();
        CommentDAO commentDAO = new CommentDAO();
        LikeDAO likeDAO = new LikeDAO();
        FriendDAO friendDAO = new FriendDAO();
        NotificationDAO notificationDAO = new NotificationDAO();

        authService = new AuthService(UserSession.getInstance(), userDAO);
        notificationService = new NotificationService(notificationDAO, authService);

        userService = new UserService(userDAO, friendDAO, postDAO, commentDAO, likeDAO, authService);
        postService = new PostService(userDAO, postDAO, commentDAO, authService, friendDAO);
        commentService = new CommentService(userDAO, commentDAO, postDAO, authService, notificationService);
        likeService = new LikeService(userDAO, postDAO, likeDAO, authService, notificationService);
        friendService = new FriendService(friendDAO, userDAO, authService, notificationService);
    }

    public static AuthService getAuthService() { return authService; }
    public static UserService getUserService() { return userService; }
    public static PostService getPostService() { return postService; }
    public static CommentService getCommentService() { return commentService; }
    public static LikeService getLikeService() { return likeService; }
    public static FriendService getFriendService() { return friendService; }
    public static NotificationService getNotificationService() { return notificationService; }
}
