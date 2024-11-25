package com.group1.interview_management.services.impl;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.group1.interview_management.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

     private final UserRepository userRepository;
     private final MessageSource messageSource;

     @Override
     public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
          return userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                         String errorMessage = messageSource.getMessage("user.notfound", new Object[]{ email }, Locale.getDefault());
                         return new UsernameNotFoundException(errorMessage);
                    });
     }

}
