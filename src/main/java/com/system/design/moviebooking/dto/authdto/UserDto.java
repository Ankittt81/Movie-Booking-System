package com.system.design.moviebooking.dto.authdto;

import com.system.design.moviebooking.model.enums.Role;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String name;
    private List<Role> roles=new ArrayList<>();
}
