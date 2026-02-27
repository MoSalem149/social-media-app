package com.socialmediaapp.DAO;

import com.socialmediaapp.Model.User;
import com.socialmediaapp.Util.DBConnection;
import com.socialmediaapp.Util.Page;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class UserDAO implements DAO<User>{
    private static final List<String> ALLOWED_SORT_FIELDS = Arrays.asList(
            "id", "name", "email", "created_at"
    );
    @Override
    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM USERS WHERE id = ?";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,id);
            ResultSet resultset = preparedStatement.executeQuery();
            if(resultset.next()){
                User user = User.builder()
                        .id(resultset.getInt("id"))
                        .name(resultset.getString("name"))
                        .email(resultset.getString("email"))
                        .password(resultset.getString("password"))
                        .bio(resultset.getString("bio"))
                        .profilePic(resultset.getString("profile_pic"))
                        .createdAt(resultset.getObject("created_at", LocalDateTime.class))
                        .build();
                return Optional.of(user);
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
                e.printStackTrace();
        }
        return Optional.empty();
    }
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM USERS WHERE email = ?";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1,email);
            ResultSet resultset = preparedStatement.executeQuery();
            if(resultset.next()){
                User user = User.builder()
                        .id(resultset.getInt("id"))
                        .name(resultset.getString("name"))
                        .email(resultset.getString("email"))
                        .password(resultset.getString("password"))
                        .bio(resultset.getString("bio"))
                        .profilePic(resultset.getString("profile_pic"))
                        .createdAt(resultset.getObject("created_at", LocalDateTime.class))
                        .build();
                return Optional.of(user);
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM USERS ORDER BY id";
        List<User> users = new ArrayList<>();
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            ResultSet resultset = preparedStatement.executeQuery();
            while (resultset.next()){
                users.add(User.builder()
                        .id(resultset.getInt("id"))
                        .name(resultset.getString("name"))
                        .email(resultset.getString("email"))
                        .password(resultset.getString("password"))
                        .bio(resultset.getString("bio"))
                        .profilePic(resultset.getString("profile_pic"))
                        .createdAt(resultset.getObject("created_at", LocalDateTime.class))
                        .build());
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return users;
    }
    @Override
    public Page<User> findAll(int pageNumber, int pageSize, String sortBy, String sortDir,
                              Optional<String> searchTerm,
                              Optional<Integer> filter1,
                              Optional<Integer> filter2) {

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            sortBy = "id";
        }
        sortDir = sortDir != null && sortDir.equalsIgnoreCase("DESC") ? "DESC" : "ASC";

        int offset = pageNumber * pageSize;

        StringBuilder sql = new StringBuilder("SELECT * FROM users");
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM users");
        List<Object> parameters = new ArrayList<>();
        boolean hasWhere = false; // track if WHERE is already added

        // searchTerm filter
        if(searchTerm.isPresent() && !searchTerm.get().isBlank()){
            sql.append(" WHERE LOWER(name) LIKE ?");
            countSql.append(" WHERE LOWER(name) LIKE ?");
            parameters.add("%" + searchTerm.get().toLowerCase() + "%");
            hasWhere = true;
        }

        // no additional numeric filters for users

        // Sorting and Pagination
        sql.append(" ORDER BY ").append(sortBy).append(" ").append(sortDir);
        sql.append(" LIMIT ? OFFSET ?");
        parameters.add(pageSize);
        parameters.add(offset);

        try (Connection connection = DBConnection.getAppDataSource().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql.toString());
             PreparedStatement countStmt = connection.prepareStatement(countSql.toString())) {

            // Set parameters for SELECT
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            List<User> users = new ArrayList<>();
            while(rs.next()){
                users.add(User.builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .email(rs.getString("email"))
                        .password(rs.getString("password"))
                        .bio(rs.getString("bio"))
                        .profilePic(rs.getString("profile_pic"))
                        .createdAt(rs.getObject("created_at", LocalDateTime.class))
                        .build());
            }

            // Set parameters for COUNT
            for(int i = 0; i < parameters.size() - 2; i++) { // exclude last 2 (LIMIT + OFFSET)
                countStmt.setObject(i + 1, parameters.get(i));
            }
            ResultSet countRs = countStmt.executeQuery();
            int totalElements = countRs.next() ? countRs.getInt(1) : 0;

            return new Page<>(users, pageNumber, pageSize, totalElements);

        } catch (SQLException e){
            e.printStackTrace();
            return new Page<>(Collections.emptyList(), pageNumber, pageSize, 0);
        }
    }
    @Override
    public boolean update(User user) {
        String sql = "UPDATE USERS SET name = ?, email = ?, password = ?, bio = ?, profile_pic = ? WHERE id = ?";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1,user.getName());
            preparedStatement.setString(2,user.getEmail());
            preparedStatement.setString(3,user.getPassword());
            preparedStatement.setString(4,user.getBio());
            preparedStatement.setString(5,user.getProfilePic());
            preparedStatement.setInt(6,user.getId());
            boolean res = preparedStatement.executeUpdate() > 0;
            System.out.println("User Updated!");
            return res;
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(User user) {
        String sql = "DELETE FROM USERS WHERE id = ?";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)){
        preparedStatement.setInt(1,user.getId());
        boolean res = preparedStatement.executeUpdate() > 0;
        System.out.println("User Deleted!");
        return res;
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean save(User user) {
        String sql = "INSERT INTO USERS(name,email,password,bio,profile_pic,created_at) VALUES(?,?,?,?,?,?)";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1,user.getName());
            preparedStatement.setString(2,user.getEmail());
            preparedStatement.setString(3,user.getPassword());
            preparedStatement.setString(4,user.getBio());
            preparedStatement.setString(5,user.getProfilePic());
            preparedStatement.setObject(6,user.getCreatedAt());
            boolean res = preparedStatement.executeUpdate() > 0;
            System.out.println("User Created!");
            return res;
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
