package com.socialmediaapp.Model;

import com.socialmediaapp.Enum.Privacy;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Post {
    private int id;
    private int userId;
    private String content;
    private String imagePath;
    private Privacy privacy;
    private LocalDateTime createdAt;
}
