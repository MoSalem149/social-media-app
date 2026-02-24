package com.socialmediaapp.Model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Like {
    private int id;
    private int user_id;
    private int post_id;
    private LocalDateTime created_at;
}
