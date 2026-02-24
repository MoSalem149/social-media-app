package com.socialmediaapp.Model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {
    private int id;
    private int user_id;
    private int post_id;
    private String content;
    private LocalDateTime created_at;
}
