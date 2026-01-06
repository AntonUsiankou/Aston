package org.ausiankou.dto.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserKafkaEvent {
    private String eventType;
    private String email;
    private String username;
    private Long userId;
}
