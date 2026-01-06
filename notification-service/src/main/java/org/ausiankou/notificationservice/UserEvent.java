package org.ausiankou.notificationservice;

import lombok.Data;

@Data
public class UserEvent {
    private String eventType;
    private String email;
    private String username;
    private Long userId;
}