package com.brass_admin_d.brass_admin_d.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeRoleResponseDto {
    String username;
    Set<String> roles;
}
