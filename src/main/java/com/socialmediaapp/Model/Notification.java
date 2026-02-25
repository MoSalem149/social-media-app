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
    private int userId;
    private int senderId;
    private Type type;
    private int referenceId;
    private boolean read;
    private LocalDateTime createdAt;
}