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
    private int userId;
    private int friendId;
    private Status status;
    private LocalDateTime createdAt;
}
