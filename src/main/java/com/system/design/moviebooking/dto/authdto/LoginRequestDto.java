package com.system.design.moviebooking.dto.authdto;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NonNull;

@Data
public class LoginRequestDto {
    @NonNull
    @Email
    private String email;
    @NonNull
    private String password;
}
