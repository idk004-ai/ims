package com.group1.interview_management.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BaseDTO {
    private int page = 0;
    private int pageSize = 10;
}
