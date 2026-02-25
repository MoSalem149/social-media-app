package com.socialmediaapp.Model;

import com.socialmediaapp.Enum.Status;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Friend {
    private int id;
    private int user_id;
    private int friend_id;
    private Status status;
    private LocalDateTime created_at;
}
