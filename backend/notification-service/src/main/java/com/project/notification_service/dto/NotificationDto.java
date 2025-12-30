package com.project.notification_service.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private String type;
    private String username;
    private String email;
    private String information;
}
