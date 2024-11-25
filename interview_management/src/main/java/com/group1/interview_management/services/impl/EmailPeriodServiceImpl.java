package com.group1.interview_management.services.impl;

import java.time.LocalDateTime;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.group1.interview_management.entities.EmailPeriod;
import com.group1.interview_management.entities.User;
import com.group1.interview_management.repositories.EmailPeriodRepository;
import com.group1.interview_management.repositories.UserRepository;
import com.group1.interview_management.services.EmailPeriodService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class EmailPeriodServiceImpl implements EmailPeriodService{
    EmailPeriodRepository emailPeriodRepository;
    UserRepository userRepository;

    @Override
    public void addResetPasswordRequest(LocalDateTime requestDate, LocalDateTime expireDate, Integer userID, boolean flag, String uuid) {
            User user = userRepository.findById(userID).orElseThrow(() -> {
                return new UsernameNotFoundException("User not found");
                });
            EmailPeriod emailPeriod = EmailPeriod.builder()
                                                 .createdAt(requestDate)
                                                 .expiredAt(expireDate)
                                                 .accessedAt(null)
                                                 .hasAccessed(flag)
                                                 .user(user)
                                                 .uuid(uuid)
                                                 .build();
            emailPeriod = emailPeriodRepository.save(emailPeriod);         
       }

   
   
    
}
