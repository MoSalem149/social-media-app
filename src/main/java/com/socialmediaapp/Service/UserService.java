package com.socialmediaapp.Service;

import com.socialmediaapp.DAO.UserDAO;
import com.socialmediaapp.Model.User;
import com.socialmediaapp.Util.ImageUploader;
import com.socialmediaapp.Util.Page;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO){
        this.userDAO = userDAO;
    }

    public User getUserById(int id){
        return userDAO.findById(id).orElseThrow(()->new IllegalArgumentException("User Not Found!"));
    }

    public User getUserByEmail(String email){
        return userDAO.findByEmail(email).orElseThrow(()->new IllegalArgumentException("User Not Found!"));
    }
    public List<User> getAllUsers(){
        return userDAO.findAll();
    }
    public Page<User> getAllUsersAsPage(int pageNumber, int pageSize, String sortBy, String sortDir,
                                        String searchTerm){
        return userDAO.findAll(pageNumber,pageSize,sortBy,sortDir,Optional.of(searchTerm),Optional.empty(),Optional.empty());
    }
    public boolean updateUser(User user, Optional<File> imageFile) throws JSONException, IOException, InterruptedException {
        if(imageFile.isPresent()){
            user.setProfilePic(ImageUploader.uploadImage(imageFile.get()));
        }
        return userDAO.update(user);
    }
    public boolean deleteUser(User user){
        return userDAO.delete(user);
    }

}
