package com.socialmediaapp.Service;

import com.socialmediaapp.DAO.CommentDAO;
import com.socialmediaapp.DAO.PostDAO;
import com.socialmediaapp.DAO.UserDAO;
import com.socialmediaapp.Model.Comment;
import com.socialmediaapp.Model.Post;
import com.socialmediaapp.Util.ImageUploader;
import com.socialmediaapp.Util.Page;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class PostService {
    private final UserDAO userDAO;
    private final PostDAO postDAO;
    private final CommentDAO commentDAO;
    private final AuthService authService;
    public PostService(UserDAO userDAO, PostDAO postDAO, CommentDAO commentDAO, AuthService authService){
        this.userDAO = userDAO;
        this.postDAO = postDAO;
        this.commentDAO = commentDAO;
        this.authService = authService;
    }

    public Post getPostById(int id){
        return postDAO.findById(id).orElseThrow(()->new IllegalArgumentException("Post Not Found!"));
    }

    public List<Post> getAllPosts(){
        return postDAO.findAll();
    }
    public Page<Post> getAllPostsAsPage(int pageNumber, int pageSize, String sortBy, String sortDir,
                                        int userId, int postId) {
        Optional<Integer> userFilter = userId > 0 ? Optional.of(userId) : Optional.empty();
        // Currently we don't filter by a specific post id in paged queries
        return postDAO.findAll(pageNumber, pageSize, sortBy, sortDir, Optional.empty(), userFilter, Optional.empty());
    }

    public boolean updatePost(Post post, Optional<File> imageFile) throws JSONException, IOException, InterruptedException {
        if(userDAO.findById(post.getUserId()).isPresent() && authService.getCurrentUser().getId() == post.getUserId()){
            if(postDAO.findById(post.getId()).isPresent()){
                if (imageFile.isPresent()) {
                    try {
                        post.setImagePath(ImageUploader.uploadImage(imageFile.get()));
                    } catch (Exception e) {
                        System.out.println("Post image update failed, keeping old image: " + e.getMessage());
                    }
                }
                postDAO.update(post);
                return true;
            }else throw new IllegalArgumentException("Post Not Found");
        }
        throw new IllegalArgumentException("Unauthorized !");
    }
    public boolean deletePost(Post post){
        if(userDAO.findById(post.getUserId()).isPresent() && authService.getCurrentUser().getId() == post.getUserId()){
            if(postDAO.findById(post.getId()).isPresent()){
                List<Comment> comments = commentDAO.findAllByPostId(post.getId());
                for(Comment comment : comments){
                    commentDAO.delete(comment);
                }
                postDAO.delete(post);
                return true;
            }else throw new IllegalArgumentException("Post Not Found");
        }
        throw new IllegalArgumentException("Unauthorized !");
    }
    public boolean createPost(Post post,Optional<File> imageFile) throws JSONException, IOException, InterruptedException {
        post.setCreatedAt(LocalDateTime.now());
        post.setUserId(authService.getCurrentUser().getId());
        if (imageFile.isPresent()) {
            try {
                post.setImagePath(ImageUploader.uploadImage(imageFile.get()));
            } catch (Exception e) {
                System.out.println("Post image upload failed, creating text-only post: " + e.getMessage());
            }
        }
        return postDAO.save(post);
    }

}
