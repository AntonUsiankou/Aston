package org.ausiankou.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserDeletedEvent extends ApplicationEvent {
    private final Long userId;
    private final String email;
    private final String userName;

    public UserDeletedEvent(Object source, Long userId, String email, String userName) {
        super(source);
        this.userId = userId;
        this.email = email;
        this.userName = userName;
    }
}