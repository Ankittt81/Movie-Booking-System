package com.system.design.moviebooking.security;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AuthUser {
    private Long id;
    private List<String> roles;

    public AuthUser(Long id, List<String> roles) {
        this.id = id;
        this.roles = roles;
    }
}
