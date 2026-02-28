package com.socialmediaapp.Util;

import com.socialmediaapp.Model.User;
import lombok.Getter;
import lombok.Setter;

public class UserSession {
    private static final UserSession instance = new UserSession();
    @Getter @Setter
    private User loggedInUser;
    private UserSession() {}
    public static UserSession getInstance() { return instance; }
    public void clearSession() { loggedInUser = null; }
}