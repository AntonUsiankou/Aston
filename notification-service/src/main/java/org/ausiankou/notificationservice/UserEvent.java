package org.ausiankou.notificationservice;

import lombok.Data;

@Data
public class UserEvent {
    private String operation;
    private String email;
    private String userName;
}