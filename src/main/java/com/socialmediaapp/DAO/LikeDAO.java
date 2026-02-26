package com.socialmediaapp.DAO;

import com.socialmediaapp.Enum.Privacy;
import com.socialmediaapp.Model.Like;
import com.socialmediaapp.Model.Post;
import com.socialmediaapp.Util.DBConnection;
import com.socialmediaapp.Util.Page;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class LikeDAO implements DAO<Like>{
    private static final List<String> ALLOWED_SORT_FIELDS = Arrays.asList(
            "id", "user_id", "post_id", "created_at"
    );

    @Override
    public Optional<Like> findById(int id) {
        String sql = "SELECT * FROM LIKES WHERE id = ?";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,id);
            ResultSet resultset = preparedStatement.executeQuery();
            if(resultset.next()){
                Like like = Like.builder()
                        .id(resultset.getInt("id"))
                        .userId(resultset.getInt("user_id"))
                        .postId(resultset.getInt("post_id"))
                        .createdAt(resultset.getObject("created_at", LocalDateTime.class))
                        .build();
                return Optional.of(like);
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Like> findAll() {
        String sql = "SELECT * FROM LIKES";
        List<Like> likes = new ArrayList<>();
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            ResultSet resultset = preparedStatement.executeQuery();
            while (resultset.next()){
                likes.add(Like.builder()
                        .id(resultset.getInt("id"))
                        .userId(resultset.getInt("user_id"))
                        .postId(resultset.getInt("post_id"))
                        .createdAt(resultset.getObject("created_at", LocalDateTime.class))
                        .build());
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return likes;
    }
    @Override
    public Page<Like> findAll(int pageNumber, int pageSize, String sortBy, String sortDir,
                              Optional<String> searchTerm,
                              Optional<Integer> userId,
                              Optional<Integer> postId) {

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            sortBy = "id";
        }
        sortDir = sortDir != null && sortDir.equalsIgnoreCase("DESC") ? "DESC" : "ASC";

        int offset = pageNumber * pageSize;

        StringBuilder sql = new StringBuilder("SELECT * FROM likes");
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM likes");
        List<Object> parameters = new ArrayList<>();
        boolean hasWhere = false; // track if WHERE is already added

        // searchTerm filter
        if(searchTerm.isPresent() && !searchTerm.get().isBlank()){
            sql.append(" WHERE LOWER(name) LIKE ?");
            countSql.append(" WHERE LOWER(name) LIKE ?");
            parameters.add("%" + searchTerm.get().toLowerCase() + "%");
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

        // filter2 (e.g., another id or status)
        if(postId.isPresent()){
            sql.append(hasWhere ? " AND " : " WHERE ");
            countSql.append(hasWhere ? " AND " : " WHERE ");
            sql.append("post_id = ?");
            countSql.append("post_id = ?");
            parameters.add(postId.get());
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
            List<Like> likes = new ArrayList<>();
            while(rs.next()){
                likes.add(Like.builder()
                        .id(rs.getInt("id"))
                        .userId(rs.getInt("user_id"))
                        .postId(rs.getInt("post_id"))
                        .createdAt(rs.getObject("created_at", LocalDateTime.class))
                        .build());
            }

            // Set parameters for COUNT
            for(int i = 0; i < parameters.size() - 2; i++) { // exclude last 2 (LIMIT + OFFSET)
                countStmt.setObject(i + 1, parameters.get(i));
            }
            ResultSet countRs = countStmt.executeQuery();
            int totalElements = countRs.next() ? countRs.getInt(1) : 0;

            return new Page<>(likes, pageNumber, pageSize, totalElements);

        } catch (SQLException e){
            e.printStackTrace();
            return new Page<>(Collections.emptyList(), pageNumber, pageSize, 0);
        }
    }

    @Override
    public boolean update(Like like) {
        return false;
    }

    @Override
    public boolean delete(Like like) {
        String sql = "DELETE FROM LIKES WHERE id = ?";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,like.getId());
            boolean res = preparedStatement.executeUpdate() > 0;
            System.out.println("Like Deleted!");
            return res;
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean save(Like like) {
        String sql = "INSERT INTO LIKES(user_id,post_id,created_at) VALUES(?,?,?)";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,like.getUserId());
            preparedStatement.setInt(2,like.getPostId());
            preparedStatement.setObject(3,like.getCreatedAt());
            boolean res = preparedStatement.executeUpdate() > 0;
            System.out.println("Like Created!");
            return res;
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
