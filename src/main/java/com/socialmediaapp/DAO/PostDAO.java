package com.socialmediaapp.DAO;

import com.socialmediaapp.Model.Post;

import java.util.List;
import java.util.Optional;

public class PostDAO implements DAO<Post>{
    @Override
    public Optional<Post> findById(int id) {
        return Optional.empty();
    }

    @Override
    public List<Post> findAll() {
        return null;
    }

    @Override
    public boolean update(Post post) {
        return false;
    }

    @Override
    public boolean delete(Post post) {
        return false;
    }

    @Override
    public boolean save(Post post) {
        return false;
    }
}
