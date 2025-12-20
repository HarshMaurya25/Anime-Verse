package com.project.auth_service.service;

import com.project.auth_service.domain.dtos.SignUpRequestDto;
import com.project.auth_service.domain.dtos.SignUpResponseDto;
import com.project.auth_service.domain.entity.UserProfile;
import com.project.auth_service.domain.enums.Roles;
import com.project.auth_service.exception.customException.UsernameOrEmailAlreadyExistsException;
import com.project.auth_service.repository.UserProfileRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class AuthControllerService {

    private final UserProfileRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;

    @Transactional
    public SignUpResponseDto signUpUser(SignUpRequestDto requestDto) {
        try{
            UserProfile user = UserProfile.builder()
                    .email(requestDto.getEmail())
                    .username(requestDto.getUsername())
                    .roles(Roles.USER)
                    .enabled(true)
                    .createdAt(LocalDateTime.now())
                    .password(passwordEncoder.encode(requestDto.getPassword()))
                    .build();

            userRepository.save(user);

            int verificationNumber = emailVerification(user.getEmail(), user.getId());
            log.info("User with username : {} and email : {} is Sign up and verification code : {}" , user.getUsername() , user.getEmail() , verificationNumber);

            return SignUpResponseDto.builder()
                    .email(requestDto.getEmail())
                    .username(user.getUsername())
                    .id(user.getId())
                    .build();

        }catch (DataIntegrityViolationException exception){
            throw new UsernameOrEmailAlreadyExistsException(requestDto.getEmail() + " & " + requestDto.getUsername());
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public int emailVerification(String email , UUID id){
        Integer randomNumber = (int) (Math.random() * 900_000) + 100_000;

        redisService.set(id.toString() , randomNumber , 300);

        return randomNumber;
    }
}
