package org.ausiankou.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserCreatedEvent extends ApplicationEvent {
    private final Long userId;
    private final String email;
    private final String userName;

    public UserCreatedEvent(Object source, Long userId, String email, String userName) {
        super(source);
        this.userId = userId;
        this.email = email;
        this.userName = userName;
    }
}
