package com.system.design.moviebooking.controller;

import com.system.design.moviebooking.dto.ApiResponse;
import com.system.design.moviebooking.dto.authdto.LoginRequestDto;
import com.system.design.moviebooking.dto.authdto.RegisterRequestDto;
import com.system.design.moviebooking.dto.authdto.UserDto;
import com.system.design.moviebooking.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UserController {
    private IUserService  userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ApiResponse register(@Valid @RequestBody RegisterRequestDto request){
        UserDto response=userService.register(request.getName(),request.getEmail(),request.getPassword());
        return new ApiResponse("User registered successfully",response);
    }

    @PostMapping("/login")
    public ApiResponse login(@Valid @RequestBody LoginRequestDto request){
        String response=userService.login(request.getEmail(), request.getPassword());
        return new ApiResponse("User login successfully",response);
    }
}
