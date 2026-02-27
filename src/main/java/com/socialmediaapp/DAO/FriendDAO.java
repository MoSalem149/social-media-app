package com.socialmediaapp.DAO;

import com.socialmediaapp.Enum.Status;
import com.socialmediaapp.Model.Friend;
import com.socialmediaapp.Model.User;
import com.socialmediaapp.Util.DBConnection;
import com.socialmediaapp.Util.Page;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class FriendDAO implements DAO<Friend>{
    private static final List<String> ALLOWED_SORT_FIELDS = Arrays.asList(
            "id", "user_id", "friend_id", "created_at"
    );
    @Override
    public Optional<Friend> findById(int id) {
        String sql = "SELECT * FROM FRIENDS WHERE id = ?";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,id);
            ResultSet resultset = preparedStatement.executeQuery();
            if(resultset.next()){
                Friend friend = Friend.builder()
                        .id(resultset.getInt("id"))
                        .userId(resultset.getInt("user_id"))
                        .friendId(resultset.getInt("friend_id"))
                        .status(Status.valueOf(resultset.getString("status").toUpperCase()))
                        .createdAt(resultset.getObject("created_at", LocalDateTime.class))
                        .build();
                return Optional.of(friend);
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Friend> findByFriendId(int friend_id) {
        String sql = "SELECT * FROM FRIENDS WHERE friend_id = ?";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,friend_id);
            ResultSet resultset = preparedStatement.executeQuery();
            if(resultset.next()){
                Friend friend = Friend.builder()
                        .id(resultset.getInt("id"))
                        .userId(resultset.getInt("user_id"))
                        .friendId(resultset.getInt("friend_id"))
                        .status(Status.valueOf(resultset.getString("status").toUpperCase()))
                        .createdAt(resultset.getObject("created_at", LocalDateTime.class))
                        .build();
                return Optional.of(friend);
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }
    public Optional<Friend> findByUserIdAndFriendId(int userId,int friend_id) {
        String sql = "SELECT * FROM friends WHERE user_id = ? AND friend_id = ?";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,userId);
            preparedStatement.setInt(2,friend_id);
            ResultSet resultset = preparedStatement.executeQuery();
            if(resultset.next()){
                Friend friend = Friend.builder()
                        .id(resultset.getInt("id"))
                        .userId(resultset.getInt("user_id"))
                        .friendId(resultset.getInt("friend_id"))
                        .status(Status.valueOf(resultset.getString("status").toUpperCase()))
                        .createdAt(resultset.getObject("created_at", LocalDateTime.class))
                        .build();
                return Optional.of(friend);
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Friend> findAll() {
        String sql = "SELECT * FROM FRIENDS";
        List<Friend> friends = new ArrayList<>();
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            ResultSet resultset = preparedStatement.executeQuery();
            while (resultset.next()){
                friends.add(Friend.builder()
                        .id(resultset.getInt("id"))
                        .userId(resultset.getInt("user_id"))
                        .friendId(resultset.getInt("friend_id"))
                        .status(Status.valueOf(resultset.getString("status").toUpperCase()))
                        .createdAt(resultset.getObject("created_at", LocalDateTime.class))
                        .build());
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return friends;
    }
    @Override
    public Page<Friend> findAll(int pageNumber, int pageSize, String sortBy, String sortDir,
                              Optional<String> searchTerm,
                              Optional<Integer> userId,
                              Optional<Integer> filter2) {

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            sortBy = "id";
        }
        sortDir = sortDir != null && sortDir.equalsIgnoreCase("DESC") ? "DESC" : "ASC";

        int offset = pageNumber * pageSize;

        StringBuilder sql = new StringBuilder("SELECT * FROM friends");
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM friends");
        List<Object> parameters = new ArrayList<>();
        boolean hasWhere = false; // track if WHERE is already added

        // searchTerm filter
        if(searchTerm.isPresent() && !searchTerm.get().isBlank()){
            sql.append(" WHERE CAST(user_id AS CHAR) LIKE ? OR CAST(friend_id AS CHAR) LIKE ?");
            countSql.append(" WHERE CAST(user_id AS CHAR) LIKE ? OR CAST(friend_id AS CHAR) LIKE ?");
            String term = "%" + searchTerm.get().toLowerCase() + "%";
            parameters.add(term);
            parameters.add(term);
            hasWhere = true;
        }

        // filter1 (e.g., user role or some id)
        if(userId.isPresent()){
            sql.append(hasWhere ? " AND " : " WHERE ");
            countSql.append(hasWhere ? " AND " : " WHERE ");
            sql.append("user_id = ?");
            countSql.append("user_id = ?");
            parameters.add(userId.get());
            hasWhere = true;
        }

        if(filter2.isPresent()){
            sql.append(hasWhere ? " AND " : " WHERE ");
            countSql.append(hasWhere ? " AND " : " WHERE ");
            sql.append("friend_id = ?");
            countSql.append("friend_id = ?");
            parameters.add(filter2.get());
        }

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
            List<Friend> friends = new ArrayList<>();
            while(rs.next()){
                friends.add(Friend.builder()
                        .id(rs.getInt("id"))
                        .userId(rs.getInt("user_id"))
                        .friendId(rs.getInt("friend_id"))
                        .status(Status.valueOf(rs.getString("status").toUpperCase()))
                        .createdAt(rs.getObject("created_at", LocalDateTime.class))
                        .build());
            }

            // Set parameters for COUNT
            for(int i = 0; i < parameters.size() - 2; i++) { // exclude last 2 (LIMIT + OFFSET)
                countStmt.setObject(i + 1, parameters.get(i));
            }
            ResultSet countRs = countStmt.executeQuery();
            int totalElements = countRs.next() ? countRs.getInt(1) : 0;

            return new Page<>(friends, pageNumber, pageSize, totalElements);

        } catch (SQLException e){
            e.printStackTrace();
            return new Page<>(Collections.emptyList(), pageNumber, pageSize, 0);
        }
    }
    public List<Friend> findAllByUserId(int user_id) {
        String sql = "SELECT * FROM friends WHERE user_id = ? OR friend_id = ?";
        List<Friend> friends = new ArrayList<>();
        try (Connection connection = DBConnection.getAppDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, user_id);
            preparedStatement.setInt(2, user_id);
            ResultSet resultset = preparedStatement.executeQuery();
            while (resultset.next()) {
                friends.add(Friend.builder()
                        .id(resultset.getInt("id"))
                        .userId(resultset.getInt("user_id"))
                        .friendId(resultset.getInt("friend_id"))
                        .status(Status.valueOf(resultset.getString("status").toUpperCase()))
                        .createdAt(resultset.getObject("created_at", LocalDateTime.class))
                        .build());
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();

        }
        return friends;
    }

    /** Returns set of user IDs that are accepted friends of the given user. */
    public java.util.Set<Integer> findFriendUserIds(int userId) {
        java.util.Set<Integer> ids = new java.util.HashSet<>();
        for (Friend f : findAllByUserId(userId)) {
            if (f.getStatus() == Status.ACCEPTED) {
                ids.add(f.getUserId() == userId ? f.getFriendId() : f.getUserId());
            }
        }
        return ids;
    }
    @Override
    public boolean update(Friend friend) {
        String sql = "UPDATE FRIENDS SET friend_id = ?, status = ? WHERE id = ?";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,friend.getFriendId());
            preparedStatement.setString(2,friend.getStatus().name().toLowerCase());
            preparedStatement.setInt(3,friend.getId());
            boolean res = preparedStatement.executeUpdate() > 0;
            System.out.println("Friend Updated!");
            return res;
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(Friend friend) {
        String sql = "DELETE FROM FRIENDS WHERE id = ?";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,friend.getId());
            boolean res = preparedStatement.executeUpdate() > 0;
            System.out.println("Friend Deleted!");
            return res;
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean save(Friend friend) {
        String sql = "INSERT INTO FRIENDS(user_id,friend_id,status,created_at) VALUES(?,?,?,?)";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,friend.getUserId());
            preparedStatement.setInt(2,friend.getFriendId());
            preparedStatement.setString(3,friend.getStatus().name().toLowerCase());
            preparedStatement.setObject(4,friend.getCreatedAt());
            boolean res = preparedStatement.executeUpdate() > 0;
            System.out.println("Friend Created!");
            return res;
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
