package com.project.auth_service.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class KafkaVerificationCodeDto {
    String username;
    String email;
    String verificationCode;
}
