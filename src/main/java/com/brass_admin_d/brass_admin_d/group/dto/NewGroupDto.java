package com.brass_admin_d.brass_admin_d.group.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NewGroupDto {
    private String groupNumber;
    private Long sessionId;
}
