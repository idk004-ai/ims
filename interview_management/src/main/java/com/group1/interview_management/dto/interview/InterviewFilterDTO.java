package com.group1.interview_management.dto.interview;

import com.group1.interview_management.dto.BaseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class InterviewFilterDTO extends BaseDTO {

     private Integer statusId;
     private Integer interviewerId;
     private String query;
}
