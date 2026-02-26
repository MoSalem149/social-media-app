package com.socialmediaapp.Service;

import com.socialmediaapp.DAO.*;
import com.socialmediaapp.Model.*;
import com.socialmediaapp.Util.ImageUploader;
import com.socialmediaapp.Util.Page;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserDAO userDAO;
    private final FriendDAO friendDAO;
    private final PostDAO postDAO;
    private final CommentDAO commentDAO;
    private final LikeDAO likeDAO;
    private final AuthService authService;

    public UserService(UserDAO userDAO, FriendDAO friendDAO, PostDAO postDAO, CommentDAO commentDAO, LikeDAO likeDAO, AuthService authService){
        this.userDAO = userDAO;
        this.friendDAO = friendDAO;
        this.postDAO = postDAO;
        this.commentDAO = commentDAO;
        this.likeDAO = likeDAO;
        this.authService=authService;
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
        if(user.getId() == authService.getCurrentUser().getId() && userDAO.findById(user.getId()).isPresent()){
            if(imageFile.isPresent()){
                user.setProfilePic(ImageUploader.uploadImage(imageFile.get()));
            }
            return userDAO.update(user);
        }
        throw new IllegalArgumentException("Unauthorized !");
    }
    public void deleteUser(User user){
        if(user.getId() == authService.getCurrentUser().getId() && userDAO.findById(user.getId()).isPresent()){
            List<Friend> friends = friendDAO.findAllByUserId(user.getId());
            List<Post> posts = postDAO.findAllByUserId(user.getId());
            List<Comment> comments = commentDAO.findAllByUserId(user.getId());
            List<Like> likes = likeDAO.findAllByUserId(user.getId());

            for(Friend friend : friends){
                friendDAO.delete(friend);
            }

            for (Like like : likes){
                likeDAO.delete(like);
            }
            for(Comment comment : comments){
                commentDAO.delete(comment);
            }
            for(Post post : posts){
                postDAO.delete(post);
            }

            userDAO.delete(user);
            authService.logout();
        }
        throw new IllegalArgumentException("Unauthorized !");
    }

}
