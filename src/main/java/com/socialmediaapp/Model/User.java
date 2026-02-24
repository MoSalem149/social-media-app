package com.socialmediaapp.Model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private String bio;
    private String profile_pic;
    private LocalDateTime created_at;
}
