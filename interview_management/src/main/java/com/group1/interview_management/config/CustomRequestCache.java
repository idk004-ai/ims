package com.group1.interview_management.config;

import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomRequestCache extends HttpSessionRequestCache {

     @Override
     public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
          // save request from browser only
          String acceptHeader = request.getHeader("Accept");
          boolean isApiRequest = acceptHeader != null
                    && (acceptHeader.contains("application/json") || acceptHeader.contains("*/*"));

          if (!isApiRequest) {
               super.saveRequest(request, response);
          }
     }
}
