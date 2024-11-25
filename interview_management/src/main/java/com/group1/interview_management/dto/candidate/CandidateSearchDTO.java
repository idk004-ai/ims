package com.group1.interview_management.dto.candidate;

import com.group1.interview_management.dto.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CandidateSearchDTO extends BaseDTO {
    private String query;
    private Integer status;
}
