package com.system.design.moviebooking.service;

import com.system.design.moviebooking.dto.authdto.LoginResponseDto;
import com.system.design.moviebooking.dto.authdto.UserDto;
import com.system.design.moviebooking.exceptions.AuthException;
import com.system.design.moviebooking.exceptions.ResourceNotFound;
import com.system.design.moviebooking.model.enums.Role;
import com.system.design.moviebooking.model.mainEntity.User;
import com.system.design.moviebooking.repository.UserRepository;
import com.system.design.moviebooking.security.JwtService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public UserDto register(String name, String email, String password) {
        String normalizedEmail = normalize(email);
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new AuthException("User with this email already exists");
        }
        User user = new User();
        user.setName(name.trim());
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(password));
        user.getRole().add(Role.USER);
        return toDto(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponseDto login(String email, String password) {
        // same message for unknown email and wrong password
        User user = userRepository.findByEmail(normalize(email))
                .orElseThrow(() -> new AuthException("Invalid email or password"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthException("Invalid email or password");
        }
        return new LoginResponseDto(jwtService.generateToken(user), toDto(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getById(Long userId) {
        return toDto(userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFound("User not found")));
    }

    private String normalize(String email) {
        return email.trim().toLowerCase();
    }

    private UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.getRoles().addAll(user.getRole());
        return dto;
    }
}