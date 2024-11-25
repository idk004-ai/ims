package com.group1.interview_management.dto.JobDTO.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.group1.interview_management.dto.BaseDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchJob extends BaseDTO {
    private String query;
    private Integer status;
}
