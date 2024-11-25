package com.group1.interview_management.services;

import java.time.LocalDateTime;

public interface EmailPeriodService {

    void addResetPasswordRequest(LocalDateTime requestDate, LocalDateTime expireDate, Integer userID, boolean flag, String uuid);
    
}
