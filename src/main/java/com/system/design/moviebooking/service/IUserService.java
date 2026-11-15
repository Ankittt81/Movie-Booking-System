package com.system.design.moviebooking.service;

import com.system.design.moviebooking.dto.authdto.LoginResponseDto;
import com.system.design.moviebooking.dto.authdto.UserDto;

public interface IUserService {
    UserDto register(String name, String email, String password);
    LoginResponseDto login(String email, String password);
    UserDto getById(Long userId);
}