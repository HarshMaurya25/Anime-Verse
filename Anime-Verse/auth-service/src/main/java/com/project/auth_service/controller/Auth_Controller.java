package com.project.auth_service.controller;

import com.project.auth_service.domain.dtos.*;
import com.project.auth_service.service.AuthControllerService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<TokenVerificationResponseDto> verifyToken(
            @Validated @RequestBody TokenVerificationRequestDto requestDto
            ){
        TokenVerificationResponseDto response = authControllerService.verifyCode(requestDto);
        return new ResponseEntity<>(response , HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> loginUser(
            @Validated @RequestBody LoginRequestDto requestDto
            ){
        LoginResponseDto responseDto = authControllerService.logIn(requestDto.getIdentifier() , requestDto.getPassword());
        return new ResponseEntity<>(responseDto , HttpStatus.OK);
    }

    @GetMapping("/access/token")
    public ResponseEntity<TokenResponseDto> getAccessToken(
            @Validated @RequestBody AccessTokenRequestDto requestDto
    ){
        TokenResponseDto responseDto = authControllerService.getAccessToken(requestDto.getToken() , requestDto.getId());
        return new ResponseEntity<>(responseDto , HttpStatus.OK);
    }

}
