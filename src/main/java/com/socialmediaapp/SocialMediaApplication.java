package com.socialmediaapp;

import com.socialmediaapp.Util.DBConnection;
import java.sql.Connection;

public class SocialMediaApplication {

    public static void main(String[] args) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            System.out.println("🎉 Ready to go!");
        }
    }
}
