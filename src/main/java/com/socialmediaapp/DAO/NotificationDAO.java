package com.socialmediaapp.DAO;

import com.socialmediaapp.Model.Notification;

import java.util.List;
import java.util.Optional;

public class NotificationDAO implements DAO<Notification>{
    @Override
    public Optional<Notification> findById(int id) {
        return Optional.empty();
    }

    @Override
    public List<Notification> findAll() {
        return null;
    }

    @Override
    public boolean update(Notification notification) {
        return false;
    }

    @Override
    public boolean delete(Notification notification) {
        return false;
    }

    @Override
    public boolean save(Notification notification) {
        return false;
    }
}
