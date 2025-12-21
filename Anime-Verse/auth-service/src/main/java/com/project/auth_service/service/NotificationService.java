package com.project.auth_service.service;

import com.project.auth_service.domain.dtos.KafkaVerificationCodeDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class NotificationService {

    private final KafkaTemplate kafkaTemplate;

    public void createEmailVerificationNotification(String email , String username , Integer verificationCode){
        String topic = "VerificationCodeNotification";
        KafkaVerificationCodeDto requestDto = KafkaVerificationCodeDto
                .builder()
                .email(email)
                .username(username)
                .verificationCode(verificationCode.toString())
                .build();
        try {
            kafkaTemplate.send(topic , requestDto);
        }catch (Exception e){
            log.error(e.getMessage());
        }

    }
}
