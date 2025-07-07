package com.brass_admin_d.brass_admin_d.exam.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScoreDto {
    private Long id;
    private Double score;
    private String teachersName;
    private String teachersNameRu;
}
