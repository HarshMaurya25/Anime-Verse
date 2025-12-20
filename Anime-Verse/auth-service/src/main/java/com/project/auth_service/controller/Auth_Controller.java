package com.project.auth_service.controller;

import com.project.auth_service.domain.dtos.SignUpRequestDto;
import com.project.auth_service.domain.dtos.SignUpResponseDto;
import com.project.auth_service.service.AuthControllerService;
import lombok.AllArgsConstructor;
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

}
