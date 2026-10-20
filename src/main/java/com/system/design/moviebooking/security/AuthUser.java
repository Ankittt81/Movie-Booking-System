package com.system.design.moviebooking.security;

import java.util.List;

public class AuthUser {
    private Long id;
    private List<String> roles;

    public AuthUser(Long id, List<String> roles) {
        this.id = id;
        this.roles = roles;
    }
}
