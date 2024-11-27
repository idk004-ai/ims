package com.group1.interview_management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
@Slf4j
public class SecurityConfig {

     private final JwtFilterConfig jwtAuthFilter;
     private final AuthenticationProvider authenticationProvider;
     private final LogoutHandler logoutHandler;
     private final RequestCache requestCache;
     private static final String[] WHITE_LIST_URL = {
               "/auth/**",
               "/common/**",
               "/user/**",
               "/static/**",
               "/assets/**",
               "/v2/api-docs",
               "/v3/api-docs",
               "/v3/api-docs/**",
               "/swagger-resources",
               "/swagger-resources/**",
               "/configuration/ui",
               "/configuration/security",
               "/swagger-ui/**",
               "/webjars/**",
               "/swagger-ui.html"
     };

     @Bean
     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
          return http
                    .cors(Customizer.withDefaults())
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(req -> req
                              .requestMatchers(WHITE_LIST_URL)
                              .permitAll()
                              .anyRequest()
                              .authenticated())
                    .formLogin(login -> login
                              .loginPage("/auth/login")
                              .permitAll())
                    .sessionManagement(session -> session
                              .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authenticationProvider(authenticationProvider)
                    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                    .requestCache(cache -> cache.requestCache(requestCache))
                    .httpBasic(Customizer.withDefaults())
                    .logout(logout -> logout.logoutUrl("/auth/logout")
                              .addLogoutHandler(logoutHandler)
                              .logoutSuccessHandler(
                                        (request, response, authentication) -> SecurityContextHolder.clearContext())
                              .permitAll())
                    .build();

     }
}
