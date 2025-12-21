package com.project.auth_service.controller;

import com.project.auth_service.domain.dtos.SignUpRequestDto;
import com.project.auth_service.domain.dtos.SignUpResponseDto;
import com.project.auth_service.domain.dtos.TokenVerificationRequest;
import com.project.auth_service.domain.dtos.TokenVerificationResponse;
import com.project.auth_service.service.AuthControllerService;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/public")
public class Auth_Controller {

    private final AuthControllerService authControllerService;

    @PostMapping("/sign-up")
    public ResponseEntity<SignUpResponseDto> signUpUser(
            @Validated @RequestBody SignUpRequestDto requestDto
            ){
        SignUpResponseDto responseDto = authControllerService.signUpUser(requestDto);

        return new ResponseEntity<>(responseDto , HttpStatus.CREATED);
    }

    @PostMapping("/verify-code")
    public ResponseEntity<TokenVerificationResponse> verifyToken(
            @Validated @RequestBody TokenVerificationRequest requestDto
            ){
        TokenVerificationResponse response = authControllerService.verifyCode(requestDto);
        return new ResponseEntity<>(response , HttpStatus.OK);
    }

}
