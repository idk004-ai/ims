package com.group1.interview_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResetPasswordRequest {
    private String newPassword;
    private String uuid;
    private String confirmPassword;
}
