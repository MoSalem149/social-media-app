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
    private final com.socialmediaapp.DAO.FriendDAO friendDAO;

    public PostService(UserDAO userDAO, PostDAO postDAO, CommentDAO commentDAO, AuthService authService,
                       com.socialmediaapp.DAO.FriendDAO friendDAO){
        this.userDAO = userDAO;
        this.postDAO = postDAO;
        this.commentDAO = commentDAO;
        this.authService = authService;
        this.friendDAO = friendDAO;
    }

    public Post getPostById(int id){
        return postDAO.findById(id).orElseThrow(()->new IllegalArgumentException("Post Not Found!"));
    }

    public List<Post> getAllPosts(){
        return postDAO.findAll();
    }

    /** News feed: posts from current user + accepted friends, newest first. */
    public List<Post> getFeedForCurrentUser() {
        int currentId = authService.getCurrentUser().getId();
        java.util.Set<Integer> feedUserIds = new java.util.HashSet<>();
        feedUserIds.add(currentId);
        feedUserIds.addAll(friendDAO.findFriendUserIds(currentId));
        return postDAO.findFeedByUserIds(feedUserIds);
    }
    public Page<Post> getAllPostsAsPage(int pageNumber, int pageSize, String sortBy, String sortDir,
                                        int userId,int postId){
        return postDAO.findAll(pageNumber,pageSize,sortBy,sortDir, Optional.empty(),Optional.of(userId),Optional.of(postId));
    }

    public boolean updatePost(Post post, Optional<File> imageFile) throws JSONException, IOException, InterruptedException {
        if(userDAO.findById(post.getUserId()).isPresent() && authService.getCurrentUser().getId() == post.getUserId()){
            if(postDAO.findById(post.getId()).isPresent()){
                if(imageFile.isPresent()){
                    post.setImagePath(ImageUploader.uploadImage(imageFile.get()));
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
        if(imageFile.isPresent()){
            post.setImagePath(ImageUploader.uploadImage(imageFile.get()));
        }
        return postDAO.save(post);
    }

}
