package com.project.auth_service.domain.dtos;

import lombok.*;

import java.util.Date;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TokenVerificationResponse {
    private String AccessToken;
    private Date TimeStampAccessToken;
    private String RefreshToken;
    private Date TimeStampRefreshToken;
}
