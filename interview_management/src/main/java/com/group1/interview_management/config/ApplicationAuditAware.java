package com.group1.interview_management.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.group1.interview_management.entities.User;

// import com.khoilnm.book.user.User;

// this class used for monitor who did what
public class ApplicationAuditAware implements AuditorAware<Integer> {

     @Override 
     public Optional<Integer> getCurrentAuditor() {
          Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
          if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
               return Optional.empty();
          }
          User user_principal = (User) authentication.getPrincipal();
          return Optional.ofNullable(user_principal.getId());
     }

}
