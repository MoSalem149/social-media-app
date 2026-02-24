package com.socialmediaapp.DAO;

import com.socialmediaapp.Model.Like;

import java.util.List;
import java.util.Optional;

public class LikeDAO implements DAO<Like>{
    @Override
    public Optional<Like> findById(int id) {
        return Optional.empty();
    }

    @Override
    public List<Like> findAll() {
        return null;
    }

    @Override
    public boolean update(Like like) {
        return false;
    }

    @Override
    public boolean delete(Like like) {
        return false;
    }

    @Override
    public boolean save(Like like) {
        return false;
    }
}
