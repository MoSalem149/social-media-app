package com.socialmediaapp.Util;

import com.socialmediaapp.Service.*;
import lombok.Getter;

@Getter
public class ServiceRegistry {
    private static ServiceRegistry instance;

    private final AuthService authService;
    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;
    private final FriendService friendService;
    private final LikeService likeService;
    private final NotificationService notificationService;

    private ServiceRegistry(AuthService authService,
                            UserService userService,
                            PostService postService,
                            CommentService commentService,
                            FriendService friendService,
                            LikeService likeService,
                            NotificationService notificationService) {
        this.authService = authService;
        this.userService = userService;
        this.postService = postService;
        this.commentService = commentService;
        this.friendService = friendService;
        this.likeService = likeService;
        this.notificationService = notificationService;
    }

    public static void initialize(AuthService authService,
                                  UserService userService,
                                  PostService postService,
                                  CommentService commentService,
                                  FriendService friendService,
                                  LikeService likeService,
                                  NotificationService notificationService) {
        instance = new ServiceRegistry(authService, userService, postService, commentService, friendService, likeService, notificationService);
    }

    public static ServiceRegistry getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ServiceRegistry was not initialized.");
        }
        return instance;
    }
}