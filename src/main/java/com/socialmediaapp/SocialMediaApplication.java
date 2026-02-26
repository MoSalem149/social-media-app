package com.socialmediaapp;

import com.socialmediaapp.DAO.PostDAO;
import com.socialmediaapp.DAO.UserDAO;
import com.socialmediaapp.Service.AuthService;
import com.socialmediaapp.Service.PostService;
import com.socialmediaapp.Service.UserService;
import com.socialmediaapp.Util.DDL;
import com.socialmediaapp.Util.UserSession;

public class SocialMediaApplication {

    public static void main(String[] args) {
        DDL.createDatabase();
        DDL.createTables();

        AuthService authService = new AuthService(UserSession.getInstance(),new UserDAO());
        UserService userService = new UserService(new UserDAO());
        PostService postService = new PostService(new UserDAO(),new PostDAO(),authService);

    }
}
