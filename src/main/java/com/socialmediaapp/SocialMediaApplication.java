package com.socialmediaapp;

import com.socialmediaapp.Util.DBConnection;
import com.socialmediaapp.Util.DDL;

public class SocialMediaApplication {

    public static void main(String[] args) {
        DDL.createDatabase();
        DDL.createTables();
    }
}
