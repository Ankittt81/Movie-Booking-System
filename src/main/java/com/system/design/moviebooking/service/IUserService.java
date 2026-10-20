package com.system.design.moviebooking.service;

import com.system.design.moviebooking.dto.authdto.UserDto;

public interface IUserService {
   UserDto register(String name,String email,String password);
   String login(String username,String password);
}
