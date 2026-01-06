package org.ausiankou.dto.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;


@Getter
public class UserDeletedEvent extends ApplicationEvent {
    private final Long userId;
    private final String email;
    private final String username;

    public UserDeletedEvent(Object source, Long userId, String email, String username) {
        super(source);
        this.userId = userId;
        this.email = email;
        this.username = username;
    }
}