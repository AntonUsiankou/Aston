package org.ausiankou.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {
    private String operation;
    private String email;
    private String userName;
    private Long userId;
}
