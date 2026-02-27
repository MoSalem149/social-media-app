package com.socialmediaapp.DAO;

import com.socialmediaapp.Util.Page;

import java.util.List;
import java.util.Optional;

public interface DAO<T> {
    Optional<T> findById(int id);
    List<T> findAll();
    default Page<T> findAll(int pageNumber, int pageSize, String sortBy, String sortDir, Optional<String> searchTerm,Optional<Integer> filterId1,Optional<Integer> filterId2){
        return null;
    }
    boolean update(T t);
    boolean delete(T t);
    boolean save(T t);
}
