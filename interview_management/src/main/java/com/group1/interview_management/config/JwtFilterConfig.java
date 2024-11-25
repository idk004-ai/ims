package com.group1.interview_management.config;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import com.group1.interview_management.common.JwtName;
import com.group1.interview_management.common.JwtTokenUtils;
import com.group1.interview_management.services.impl.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
// import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilterConfig extends OncePerRequestFilter {

     private final JwtService jwtService;
     private final UserDetailsService userDetailsService;
     // private final RequestCache requestCache;
     private final List<String> publicPaths = Arrays.asList(
               "/auth",
               "/static",
               "/common",
               "/assets"
               );

     private boolean isPublicPath(String path) {
          return publicPaths.stream().anyMatch(path::startsWith);
     }

     @Override
     protected void doFilterInternal(
               @NonNull HttpServletRequest request,
               @NonNull HttpServletResponse response,
               @NonNull FilterChain filterChain) throws ServletException, IOException {
          if (isPublicPath(request.getServletPath())) {
               filterChain.doFilter(request, response);
               return;
          }

          final String jwt = JwtTokenUtils.extractTokenFromCookie(request, JwtName.ACCESS_TOKEN.getValue());
          if (jwt == null) {
               // save the request
               // if (!request.getRequestURI().startsWith("/auth")) {
               //      requestCache.saveRequest(request, response);
               // }
               filterChain.doFilter(request, response);
               return;
          }
          final String userEmail = jwtService.extractSubject(jwt);
          if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
               UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
               if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                              userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
               }
          }

          filterChain.doFilter(request, response);
     }

}
