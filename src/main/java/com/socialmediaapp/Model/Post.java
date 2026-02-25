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
    private int user_id;
    private String content;
    private String image_path;
    private Privacy privacy;
    private LocalDateTime created_at;
}
