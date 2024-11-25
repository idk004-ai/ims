package com.group1.interview_management.config;

import java.util.*;

import org.springframework.http.HttpHeaders;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import lombok.RequiredArgsConstructor;

@EnableRetry
@Configuration
@RequiredArgsConstructor
public class BeanConfig {

     private final UserDetailsService userDetailsService; // lấy thông tin user sau khi đăng nhập

     @Bean
     public AuthenticationProvider authenticationProvider() {
          DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
          authProvider.setUserDetailsService(userDetailsService);
          authProvider.setPasswordEncoder(passwordEncoder());
          return authProvider;
     }

     @Bean
     public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
          return config.getAuthenticationManager();
     }

     @Bean
     public PasswordEncoder passwordEncoder() {
          return new BCryptPasswordEncoder();
     }

     @Bean
     public AuditorAware<Integer> auditorAware() {
          return new ApplicationAuditAware();
     }

     @Bean
     public ModelMapper modelMapper() {
          return new ModelMapper();
     }

     @Bean
     public TaskScheduler taskScheduler() {
          ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
          taskScheduler.setPoolSize(10);
          taskScheduler.setThreadNamePrefix("scheduled-task-");
          return taskScheduler;
     }

     @Bean
     public RequestCache requestCache() {
          return new HttpSessionRequestCache();
     }

     @Bean
     public CorsFilter corsFilter() {
          final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
          final CorsConfiguration config = new CorsConfiguration();
          config.setAllowCredentials(true);
          config.setAllowedOrigins(Collections.singletonList("https://jobnet.click"));
          config.setAllowedHeaders(Arrays.asList(
                    HttpHeaders.ORIGIN,
                    HttpHeaders.CONTENT_TYPE,
                    HttpHeaders.ACCEPT,
                    HttpHeaders.AUTHORIZATION));
          config.setAllowedMethods(Arrays.asList(
                    "GET",
                    "POST",
                    "DELETE",
                    "PUT",
                    "PATCH",
                    "OPTIONS"));
          source.registerCorsConfiguration("/**", config);
          return new CorsFilter(source);

     }

     @Bean
     public JavaMailSender getJavaMailSender() {
          JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
          mailSender.setHost("smtp.gmail.com");
          mailSender.setPort(587);

          mailSender.setUsername("minhkhoilenhat04@gmail.com");
          mailSender.setPassword("msfi tdvq dyru czdg");

          Properties props = mailSender.getJavaMailProperties();
          props.put("mail.transport.protocol", "smtp");
          props.put("mail.smtp.auth", "true");
          props.put("mail.smtp.starttls.enable", "true");
          props.put("mail.debug", "true");

          return mailSender;
     }
}
