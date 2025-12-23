package com.project.user_service.controller;

import com.project.user_service.domain.dto.request.CreateUserDetailsRequestDto;
import com.project.user_service.domain.dto.response.UserProfileResponseDto;
import com.project.user_service.domain.secuirtyEntity.UserDetailCustom;
import com.project.user_service.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('MODERATOR') or authentication.principal.id.equals(#requestDto.id)")
    @PostMapping(path = "/profile/user/create")
    public ResponseEntity<UserProfileResponseDto> createUser(
            @RequestPart("data") CreateUserDetailsRequestDto requestDto,
            @RequestPart("file") MultipartFile file
    ) {
        UserProfileResponseDto responseDto = userService.createUser(requestDto, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PreAuthorize("hasRole('MODERATOR') or authentication.principal.id.equals(#id)")
    @PostMapping("/profile/img/upload")
    public ResponseEntity<?> uploadProfilePic(
            @RequestPart MultipartFile file,
            @RequestParam UUID id
    ){
        userService.profileImageUpload(file , id);
        return ResponseEntity.ok().body("Image Updated");
    }

    @PreAuthorize("hasAuthority('user:get')")
    @GetMapping("/profile/get")
    public ResponseEntity<UserProfileResponseDto> getUser(
            @RequestParam UUID id
    ) {
        UserProfileResponseDto responseDto = userService.getUserDetail(id);
        return ResponseEntity.ok()
                .body(responseDto);
    }
}
