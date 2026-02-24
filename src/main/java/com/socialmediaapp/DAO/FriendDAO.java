package com.socialmediaapp.DAO;

import com.socialmediaapp.Model.Friend;

import java.util.List;
import java.util.Optional;

public class FriendDAO implements DAO<Friend>{
    @Override
    public Optional<Friend> findById(int id) {
        return Optional.empty();
    }

    @Override
    public List<Friend> findAll() {
        return null;
    }

    @Override
    public boolean update(Friend friend) {
        return false;
    }

    @Override
    public boolean delete(Friend friend) {
        return false;
    }

    @Override
    public boolean save(Friend friend) {
        return false;
    }
}
