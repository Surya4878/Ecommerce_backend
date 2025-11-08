package com.ecommerce.dto;

import com.ecommerce.entity.Role;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private Role role;
}
