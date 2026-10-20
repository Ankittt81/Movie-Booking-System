package com.system.design.moviebooking.dto.authdto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDto {
    @NonNull
    private String name;
    @NonNull
    @Email
    private String email;
    @NonNull
    private String password;
}
