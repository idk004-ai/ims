package com.group1.interview_management.common;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class JwtTokenUtils {

     public static String extractTokenFromCookie(HttpServletRequest request, String tokenName) {
          Cookie[] cookies = request.getCookies();
          if (cookies != null) {
               for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(tokenName)) {
                         return cookie.getValue();
                    }
               }
          }
          return null;
     }
}
