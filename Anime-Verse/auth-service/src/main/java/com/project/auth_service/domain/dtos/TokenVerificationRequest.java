package com.project.auth_service.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenVerificationRequest {

    @NonNull
    private UUID id;

    @NotBlank
    private String role;

    @NonNull
    private int code;

}
