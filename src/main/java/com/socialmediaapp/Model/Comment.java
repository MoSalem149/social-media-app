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
    private int userId;
    private int postId;
    private String content;
    private LocalDateTime createdAt;
}
