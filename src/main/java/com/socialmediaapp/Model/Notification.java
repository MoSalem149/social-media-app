package com.socialmediaapp.Model;

import com.socialmediaapp.Enum.Type;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {
    private int id;
    private int user_id;
    private int sender_id;
    private Type type;
    private int reference_id;
    private boolean is_read;
    private LocalDateTime created_at;
}
