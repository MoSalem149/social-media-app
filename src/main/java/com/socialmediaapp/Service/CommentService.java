package com.socialmediaapp.Service;

import com.socialmediaapp.DAO.CommentDAO;
import com.socialmediaapp.DAO.PostDAO;
import com.socialmediaapp.DAO.UserDAO;
import com.socialmediaapp.Model.Comment;
import com.socialmediaapp.Util.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class CommentService {
    private final UserDAO userDAO;
    private final CommentDAO commentDAO;
    private final PostDAO postDAO;
    private final AuthService authService;

    public CommentService(UserDAO userDAO, CommentDAO commentDAO, PostDAO postDAO, AuthService authService) {
        this.userDAO = userDAO;
        this.commentDAO = commentDAO;
        this.postDAO = postDAO;
        this.authService = authService;
    }

    public Comment getCommentById(int id){
        return commentDAO.findById(id).orElseThrow(()->new IllegalArgumentException("Comment not Found!"));
    }
    public List<Comment> getAllComments(){
        return commentDAO.findAll();
    }

    public Page<Comment> getAllCommentsAsPage(int pageNumber, int pageSize, String sortBy, String sortDir,
                                              int userId,int postId){
        return commentDAO.findAll(pageNumber,pageSize,sortBy,sortDir, Optional.empty(),Optional.of(userId),Optional.of(postId));
    }

    public boolean updateComment(Comment comment){
        if(comment.getUserId() == authService.getCurrentUser().getId() && postDAO.findById(comment.getPostId()).isPresent() && userDAO.findById(comment.getId()).isPresent()){
           if(commentDAO.findById(comment.getId()).isPresent()){
               return commentDAO.update(comment);
           }else throw new IllegalArgumentException("Comment Not Found!");
        }
        throw new IllegalArgumentException("Unauthorized !");
    }

    public boolean deleteComment(Comment comment){
        if(comment.getUserId() == authService.getCurrentUser().getId() && postDAO.findById(comment.getPostId()).isPresent() && userDAO.findById(comment.getId()).isPresent()){
            if(commentDAO.findById(comment.getId()).isPresent()){
                return commentDAO.delete(comment);
            }else throw new IllegalArgumentException("Comment Not Found!");
        }
        throw new IllegalArgumentException("Unauthorized !");
    }
    public boolean createComment(Comment comment){
        if(comment.getUserId() == authService.getCurrentUser().getId() && postDAO.findById(comment.getPostId()).isPresent() && userDAO.findById(comment.getUserId()).isPresent()){
            comment.setUserId(authService.getCurrentUser().getId());
            comment.setCreatedAt(LocalDateTime.now());
            return commentDAO.save(comment);
        }
        throw new IllegalArgumentException("Unauthorized !");
    }
}
