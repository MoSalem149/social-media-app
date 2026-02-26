package com.socialmediaapp.Util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHashing {
    public static String hashPassword(String password){
        return BCrypt.hashpw(password,BCrypt.gensalt());
    }
    public static boolean verifyPassword(String rowPassword,String hashedPassword){
        return BCrypt.checkpw(rowPassword,hashedPassword);
    }
}
