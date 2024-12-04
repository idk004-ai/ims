package com.group1.interview_management.dto.JobDTO.request;

import com.group1.interview_management.dto.BaseDTO;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchJob extends BaseDTO {
    private String query;
    private Integer status;
}
