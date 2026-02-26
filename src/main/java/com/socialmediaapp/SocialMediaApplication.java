package com.socialmediaapp;

import com.socialmediaapp.DAO.*;
import com.socialmediaapp.Service.*;
import com.socialmediaapp.Util.DDL;
import com.socialmediaapp.Util.UserSession;

public class SocialMediaApplication {

    public static void main(String[] args) {
        DDL.createDatabase();
        DDL.createTables();

        AuthService authService = new AuthService(UserSession.getInstance(),new UserDAO());
        UserService userService = new UserService(new UserDAO(),new FriendDAO(),new PostDAO(),new CommentDAO(),new LikeDAO(),authService);
        PostService postService = new PostService(new UserDAO(),new PostDAO(),new CommentDAO(),authService);
        CommentService commentService = new CommentService(new UserDAO(),new CommentDAO(),new PostDAO(),authService);
        FriendService friendService = new FriendService(new FriendDAO(),new UserDAO(),authService);
        LikeService likeService = new LikeService(new UserDAO(),new PostDAO(),new LikeDAO(),authService);

    }
}
