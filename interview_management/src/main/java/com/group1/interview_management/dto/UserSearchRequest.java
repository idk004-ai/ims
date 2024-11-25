package com.group1.interview_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchRequest {
    private String query;
    private String status;
    private int page;
    private int size;
}