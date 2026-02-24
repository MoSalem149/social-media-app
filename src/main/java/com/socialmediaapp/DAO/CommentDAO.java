package com.socialmediaapp.DAO;

import com.socialmediaapp.Model.Comment;

import java.util.List;
import java.util.Optional;

public class CommentDAO implements DAO<Comment>{
    @Override
    public Optional<Comment> findById(int id) {
        return Optional.empty();
    }

    @Override
    public List<Comment> findAll() {
        return null;
    }

    @Override
    public boolean update(Comment comment) {
        return false;
    }

    @Override
    public boolean delete(Comment comment) {
        return false;
    }

    @Override
    public boolean save(Comment comment) {
        return false;
    }
}
