package com.group1.interview_management.dto.interview;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter @Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class EditInterviewDTO extends CreateInterviewDTO {
     Integer interview_result;
     String status; // fixed
}
