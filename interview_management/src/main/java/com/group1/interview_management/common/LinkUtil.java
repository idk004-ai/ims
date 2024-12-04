package com.group1.interview_management.common;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.group1.interview_management.entities.EmailPeriod;
import com.group1.interview_management.repositories.EmailPeriodRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class LinkUtil {
    EmailPeriodRepository emailPeriodRepository;

     public static String generateLink(String uuid) {
        String baseUrl = "https://jobnet.click/api/v1/user/reset-password/"; 
        return baseUrl + uuid;
    }

    public boolean isLinkValid(String uuid) {
        EmailPeriod emailPeriod = emailPeriodRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("Link not found"));
        LocalDateTime now = LocalDateTime.now();
        if (emailPeriod.getExpiredAt().isBefore(now) || emailPeriod.isHasAccessed()) {
            return false; 
        }
        emailPeriod.setAccessedAt(now);
        emailPeriod.setHasAccessed(true);
        emailPeriodRepository.save(emailPeriod);
        return true;
    }
}
