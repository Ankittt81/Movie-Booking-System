package com.system.design.moviebooking.controller;

import com.system.design.moviebooking.dto.ApiResponse;
import com.system.design.moviebooking.dto.authdto.LoginRequestDto;
import com.system.design.moviebooking.dto.authdto.LoginResponseDto;
import com.system.design.moviebooking.dto.authdto.RegisterRequestDto;
import com.system.design.moviebooking.dto.authdto.UserDto;
import com.system.design.moviebooking.security.AuthUser;
import com.system.design.moviebooking.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {
    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> register(@Valid @RequestBody RegisterRequestDto request) {
        UserDto response = userService.register(request.getName(), request.getEmail(), request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("User registered successfully", response));
    }

    // token goes in the Authorization header, user details in the body
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserDto>> login(@Valid @RequestBody LoginRequestDto request) {
        LoginResponseDto result = userService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + result.getToken())
                .body(new ApiResponse<>("Login successful", result.getUser()));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> me(@AuthenticationPrincipal AuthUser authUser) {
        return ResponseEntity.ok(new ApiResponse<>("Fetched current user", userService.getById(authUser.getId())));
    }
}