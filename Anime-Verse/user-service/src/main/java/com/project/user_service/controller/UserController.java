package com.project.user_service.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class UserController {

    @PreAuthorize("hasAnyAuthority('user:get')")
    @GetMapping("/hello")
    public String getMethodName(
            HttpServletRequest request) {
        return request.getHeader("User-Id");
    }
}
