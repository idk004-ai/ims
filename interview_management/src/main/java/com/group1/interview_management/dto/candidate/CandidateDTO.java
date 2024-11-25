package com.group1.interview_management.dto.candidate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateDTO {
        private Integer id;
        private String fullName;
        private String email;
        private String phone;
        private String currentPosition;
        private String ownerHR;
        private String status;
}
