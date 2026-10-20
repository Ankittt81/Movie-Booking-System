package com.system.design.moviebooking.service;

import com.system.design.moviebooking.dto.authdto.UserDto;
import com.system.design.moviebooking.exceptions.AuthException;
import com.system.design.moviebooking.model.enums.Role;
import com.system.design.moviebooking.model.mainEntity.User;
import com.system.design.moviebooking.repository.UserRepository;
import com.system.design.moviebooking.security.JwtService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService{
    private UserRepository userRepository;
    private JwtService  jwtService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtService = jwtService;
    }
    @Override
    public UserDto register(String name, String email, String password) {
        String normalizeEmail=email.trim().toLowerCase();
        boolean exists=userRepository.existsByEmail(normalizeEmail);
        if(exists){
            throw new AuthException("User with this email already exists");
        }
        User user=new User();
        user.setName(name);
        user.setEmail(normalizeEmail);
        String hashPassword=bCryptPasswordEncoder.encode(password);
        user.setPassword(hashPassword);
        user.getRole().add(Role.USER);
        userRepository.save(user);
        UserDto userDto=new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());
        userDto.getRoles().addAll(user.getRole());
        return userDto;
    }

    @Override
    public String login(String email, String password) {
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new AuthException("User Already Exist"));
        boolean match=bCryptPasswordEncoder.matches(password,user.getPassword());
        if(!match){
            throw new AuthException("Password is incorrect");
        }
        String token= jwtService.generateToken(user);
        UserDto userDto=new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());
        userDto.getRoles().addAll(user.getRole());
        return token;
    }


}
