package com.group1.interview_management.config;

import java.util.*;

import org.springframework.http.HttpHeaders;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import lombok.RequiredArgsConstructor;

@EnableCaching
@EnableRetry
@Configuration
@RequiredArgsConstructor
public class BeanConfig {

     private final UserDetailsService userDetailsService; // lấy thông tin user sau khi đăng nhập
     @Value("${spring.graphql.cors.allowed-origins}")
     private String[] allowedOrigins;
     @Value("${spring.graphql.cors.allowed-methods}")
     private String[] allowedMethods;
     @Value("${spring.graphql.cors.allowed-origin-patterns}")
     private String allowedOriginPatterns;

     @Value("${spring.mail.host}")
     private String mailHost;
     @Value("${spring.mail.port}")
     private Integer mailPort;
     @Value("${spring.mail.username}")
     private String mailUsername;
     @Value("${spring.mail.password}")
     private String mailPassword;
     @Value("${spring.mail.protocol}")
     private String mailProtocol;
     @Value("${spring.mail.properties.mail.smtp.auth}")
     private String mailAuth;
     @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
     private String mailStartTlsEnable;
     @Value("${spring.mail.debug}")
     private String mailDebug;

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
     public CorsFilter corsFilter() {
          final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
          final CorsConfiguration config = new CorsConfiguration();
          config.setAllowCredentials(true);
          config.setAllowedOrigins(Arrays.asList(allowedOrigins));
          config.setAllowedHeaders(Arrays.asList(
                    HttpHeaders.ORIGIN,
                    HttpHeaders.CONTENT_TYPE,
                    HttpHeaders.ACCEPT,
                    HttpHeaders.AUTHORIZATION));
          config.setAllowedMethods(Arrays.asList(allowedMethods));
          source.registerCorsConfiguration(allowedOriginPatterns, config);
          return new CorsFilter(source);

     }

     @Bean
     public JavaMailSender getJavaMailSender() {
          JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
          mailSender.setHost(mailHost);
          mailSender.setPort(mailPort);

          mailSender.setUsername(mailUsername);
          mailSender.setPassword(mailPassword);

          Properties props = mailSender.getJavaMailProperties();
          props.put("mail.transport.protocol", mailProtocol);
          props.put("mail.smtp.auth", mailAuth);
          props.put("mail.smtp.starttls.enable", mailStartTlsEnable);
          props.put("mail.debug", mailDebug);

          return mailSender;
     }
}
