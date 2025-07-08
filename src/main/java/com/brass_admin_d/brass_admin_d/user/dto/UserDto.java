package com.brass_admin_d.brass_admin_d.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {
    Long id;
    String username;
    Set<String> roles;
    Long departmentId;
}
