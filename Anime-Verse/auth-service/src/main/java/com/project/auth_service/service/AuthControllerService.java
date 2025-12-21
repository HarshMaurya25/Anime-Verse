package com.project.auth_service.service;

import com.project.auth_service.domain.dtos.SignUpRequestDto;
import com.project.auth_service.domain.dtos.SignUpResponseDto;
import com.project.auth_service.domain.dtos.TokenVerificationRequest;
import com.project.auth_service.domain.dtos.TokenVerificationResponse;
import com.project.auth_service.domain.entity.RefreshToken;
import com.project.auth_service.domain.entity.UserProfile;
import com.project.auth_service.domain.enums.Roles;
import com.project.auth_service.exception.customException.InvalidVerificationCodeException;
import com.project.auth_service.exception.customException.UserNotFoundException;
import com.project.auth_service.exception.customException.UsernameOrEmailAlreadyExistsException;
import com.project.auth_service.exception.customException.VerificationCodeExpiredException;
import com.project.auth_service.repository.RefreshTokenRepo;
import com.project.auth_service.repository.UserProfileRepository;
import io.jsonwebtoken.JwtException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class AuthControllerService {

    private final UserProfileRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;
    private final NotificationService notificationService;
    private final JwtService jwtService;
    private final RefreshTokenRepo refreshTokenRepo;

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

            int verificationNumber = emailVerification(user.getEmail() , user.getUsername(), user.getId());
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

    public int emailVerification(String email , String username , UUID id){
        Integer randomNumber = (int) (Math.random() * 900_000) + 100_000;

        redisService.set(id.toString() , randomNumber , 300);
        notificationService.createEmailVerificationNotification(email , username , randomNumber);

        return randomNumber;
    }

    @Transactional
    public TokenVerificationResponse verifyCode(TokenVerificationRequest requestDto) {

        Integer redisCode = redisService.get(
                requestDto.getId().toString(),
                Integer.class
        );

        if (redisCode == null) {
            throw new VerificationCodeExpiredException("Verification code expired or invalid");
        }

        if (!redisCode.equals(requestDto.getCode())) {
            throw new InvalidVerificationCodeException("Invalid verification code");
        }

        UserProfile user = userRepository.findById(requestDto.getId())
                .orElseThrow(() -> new UserNotFoundException(requestDto.getId().toString()));

        TokenVerificationResponse tokens =
                jwtService.getTokens(requestDto.getId(), requestDto.getRole());

        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken(tokens.getRefreshToken())
                .expireDate(tokens.getTimeStampRefreshToken())
                .enable(true)
                .userProfile(user)
                .build();

        refreshTokenRepo.save(refreshToken);

        user.setEnabled(true);

        redisService.delete(requestDto.getId().toString());

        return tokens;
    }

}
