package com.socialmediaapp.DAO;

import java.util.List;
import java.util.Optional;

public interface DAO<T> {
    Optional<T> findById(int id);
    List<T> findAll();
    boolean update(T t);
    boolean delete(T t);
    boolean save(T t);
}
