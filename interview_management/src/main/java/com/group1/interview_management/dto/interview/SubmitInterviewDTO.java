package com.group1.interview_management.dto.interview;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubmitInterviewDTO {
     @Min(value = 1, message = "{ME002}")
     private Integer interview_result;
     private String note;
}
