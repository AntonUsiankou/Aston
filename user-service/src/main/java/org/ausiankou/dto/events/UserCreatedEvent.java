package org.ausiankou.dto.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserCreatedEvent extends ApplicationEvent {
    private final Long userId;
    private final String email;
    private final String username;

    public UserCreatedEvent(Object source, Long userId, String email, String username) {
        super(source);
        this.userId = userId;
        this.email = email;
        this.username = username;
    }
}
