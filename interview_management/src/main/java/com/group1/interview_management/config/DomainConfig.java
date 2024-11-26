package com.group1.interview_management.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {
     @Value("${app.domain}")
     private String domainUrl;

     @Value("${spring.profiles.active:prod}")
     private String activeProfile;

     @Bean
     public String domainUrl() {
          return domainUrl;
     }
}
