package com.socialmediaapp.DAO;

import com.socialmediaapp.Model.User;

import java.util.List;
import java.util.Optional;

public class UserDAO implements DAO<User>{
    @Override
    public Optional<User> findById(int id) {
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public boolean update(User user) {
        return false;
    }

    @Override
    public boolean delete(User user) {
        return false;
    }

    @Override
    public boolean save(User user) {
        return false;
    }
}
