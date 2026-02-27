package com.socialmediaapp.Service;

import com.socialmediaapp.DAO.UserDAO;
import com.socialmediaapp.Model.User;
import com.socialmediaapp.Util.ImageUploader;
import com.socialmediaapp.Util.PasswordHashing;
import com.socialmediaapp.Util.UserSession;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

public class AuthService {
    private final UserSession userSession ;
    private final UserDAO userDAO;

    public AuthService(UserSession userSession,UserDAO userDAO){
        this.userSession = userSession;
        this.userDAO = userDAO;
    }
    public User getCurrentUser(){
        return userSession.getLoggedInUser();
    }
    public void setCurrentUser(User user){
        userSession.setLoggedInUser(user);
    }
    public void logout(){
        userSession.clearSession();
    }
    public boolean login(String email,String password){
        User user = userDAO.findByEmail(email).orElseThrow(()->new IllegalArgumentException("Invalid email or password!"));
        if(!PasswordHashing.verifyPassword(password,user.getPassword())){
            throw new IllegalArgumentException("Invalid email or password!");
        }
        setCurrentUser(user);
        return true;
    }
    public boolean register(User user, Optional<File> imageFile) throws JSONException, IOException, InterruptedException {
        if(userDAO.findByEmail(user.getEmail()).isPresent()){
            throw new IllegalArgumentException("This Email Already Exists!");
        }
        user.setPassword(PasswordHashing.hashPassword(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        if(imageFile.isPresent()){
            user.setProfilePic(ImageUploader.uploadImage(imageFile.get()));
        }
        userDAO.save(user);
        // Load saved user from DB (with generated id) and set as current user so main screen works
        User saved = userDAO.findByEmail(user.getEmail()).orElseThrow(() -> new IllegalStateException("User not found after save"));
        setCurrentUser(saved);
        return true;
    }
}
