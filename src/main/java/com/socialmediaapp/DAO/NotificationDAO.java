package com.socialmediaapp.DAO;

import com.socialmediaapp.Enum.Type;
import com.socialmediaapp.Model.Notification;
import com.socialmediaapp.Util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NotificationDAO implements DAO<Notification> {

    private Notification mapRow(ResultSet rs) throws SQLException {
        return Notification.builder()
                .id(rs.getInt("id"))
                .userId(rs.getInt("user_id"))
                .senderId(rs.getInt("sender_id"))
                .type(Type.valueOf(rs.getString("type").toUpperCase()))
                .referenceId(rs.getInt("reference_id"))
                .read(rs.getBoolean("is_read"))
                .createdAt(rs.getObject("created_at", LocalDateTime.class))
                .build();
    }

    @Override
    public Optional<Notification> findById(int id) {
        String sql = "SELECT * FROM notifications WHERE id = ?";
        try (Connection connection = DBConnection.getAppDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return Optional.of(mapRow(rs));

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Notification> findAll() {
        String sql = "SELECT * FROM notifications";
        List<Notification> notifications = new ArrayList<>();

        try (Connection connection = DBConnection.getAppDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next())
                notifications.add(mapRow(rs));

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return notifications;
    }

    public List<Notification> findAllByUserId(int userId) {
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        List<Notification> notifications = new ArrayList<>();

        try (Connection connection = DBConnection.getAppDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next())
                notifications.add(mapRow(rs));

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return notifications;
    }

    @Override
    public boolean save(Notification notification) {
        String sql = """
                INSERT INTO notifications
                (user_id, sender_id, type, reference_id, is_read)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getAppDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, notification.getUserId());
            ps.setInt(2, notification.getSenderId());
            ps.setString(3, notification.getType().name().toLowerCase());
            ps.setInt(4, notification.getReferenceId());
            ps.setBoolean(5, notification.isRead());

            boolean res = ps.executeUpdate() > 0;
            System.out.println("Notification Created!");
            return res;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    @Override
    public boolean update(Notification notification) {
        String sql = "UPDATE notifications SET is_read = ? WHERE id = ?";

        try (Connection connection = DBConnection.getAppDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setBoolean(1, notification.isRead());
            ps.setInt(2, notification.getId());

            boolean res = ps.executeUpdate() > 0;
            System.out.println("Notification Updated!");
            return res;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    public boolean markAllAsRead(int userId) {
        String sql = "UPDATE notifications SET is_read = true WHERE user_id = ?";

        try (Connection connection = DBConnection.getAppDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);

            boolean res = ps.executeUpdate() > 0;
            System.out.println("All Notifications Marked as Read!");
            return res;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    @Override
    public boolean delete(Notification notification) {

        String sql = "DELETE FROM notifications WHERE id = ?";

        try (Connection connection = DBConnection.getAppDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, notification.getId());

            boolean res = ps.executeUpdate() > 0;
            System.out.println("Notification Deleted!");
            return res;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }
}