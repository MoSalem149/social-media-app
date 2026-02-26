package com.socialmediaapp.Service;

import com.socialmediaapp.DAO.LikeDAO;
import com.socialmediaapp.DAO.PostDAO;
import com.socialmediaapp.DAO.UserDAO;
import com.socialmediaapp.Model.Like;
import com.socialmediaapp.Util.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class LikeService {
    private final UserDAO userDAO;
    private final PostDAO postDAO;
    private final LikeDAO likeDAO;
    private final AuthService authService;

    public LikeService(UserDAO userDAO, PostDAO postDAO, LikeDAO likeDAO, AuthService authService) {
        this.userDAO = userDAO;
        this.postDAO = postDAO;
        this.likeDAO = likeDAO;
        this.authService = authService;
    }

    public Like getLikeById(int id){
        return likeDAO.findById(id).orElseThrow(()->new IllegalArgumentException("Not Found!"));
    }

    public List<Like> getAllLikes(){
        return likeDAO.findAll();
    }

    public List<Like> getAllLikesByUserId(int userId){
        return likeDAO.findAllByUserId(userId);
    }
    public List<Like> getAllLikesByPostId(int postId){
        return likeDAO.findAllByPostId(postId);
    }

    public Page<Like> getAllLikesAsPage(int pageNumber, int pageSize, String sortBy, String sortDir,
                                        int userId,int postId){
        return likeDAO.findAll(pageNumber,pageSize,sortBy,sortDir, Optional.empty(),Optional.of(userId),Optional.of(postId));
    }

    public boolean deleteLike(Like like){
        if(like.getUserId() == authService.getCurrentUser().getId() && userDAO.findById(like.getUserId()).isPresent()){
            if(likeDAO.findById(like.getId()).isPresent()){
                return likeDAO.delete(like);
            }
        }
        throw new IllegalArgumentException("Unauthorized !");
    }

    public boolean createLike(Like like){
        if(like.getUserId() == authService.getCurrentUser().getId() && userDAO.findById(like.getUserId()).isPresent()){
            if(postDAO.findById(like.getPostId()).isPresent()){
                like.setCreatedAt(LocalDateTime.now());
                return likeDAO.save(like);
            }else throw new IllegalArgumentException("Post Not Found!");
        }
        throw new IllegalArgumentException("Unauthorized !");
    }
}
