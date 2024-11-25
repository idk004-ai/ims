package com.group1.interview_management.services.impl;

// import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.group1.interview_management.dto.JwtTokenDTO;
import com.group1.interview_management.dto.LoginDTO;
import com.group1.interview_management.dto.RegistrationDTO;
import com.group1.interview_management.entities.EmailToken;
import com.group1.interview_management.entities.User;
import com.group1.interview_management.repositories.EmailTokenRepository;
import com.group1.interview_management.repositories.MasterRepository;
import com.group1.interview_management.repositories.UserRepository;

import io.jsonwebtoken.ExpiredJwtException;

import com.group1.interview_management.common.EmailTemplateName;
import com.group1.interview_management.common.JwtName;
import com.group1.interview_management.common.JwtTokenUtils;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AuthenticationService implements LogoutHandler {

     private UserRepository userRepository;
     private MasterRepository masterRepository;
     private MessageSource messageSource;
     private PasswordEncoder passwordEncoder;
     private EmailService emailService;
     private JwtService jwtService;
     private AuthenticationManager authenticationManager;
     private EmailTokenRepository emailTokenRepository;
     private static final String ADMIN_ROLE = "ADMIN";

     public AuthenticationService(
               UserRepository userRepository,
               MasterRepository masterRepository,
               MessageSource messageSource,
               PasswordEncoder passwordEncoder,
               EmailTokenRepository emailTokenRepository,
               EmailService emailService,
               JwtService jwtService,
               @Lazy AuthenticationManager authenticationManager) {
          this.userRepository = userRepository;
          this.masterRepository = masterRepository;
          this.messageSource = messageSource;
          this.passwordEncoder = passwordEncoder;
          this.emailTokenRepository = emailTokenRepository;
          this.emailService = emailService;
          this.jwtService = jwtService;
          this.authenticationManager = authenticationManager;
     }

     // @Value("${application.mailing.frontend.activation-url}")
     // private String activationUrl;

     public void register(@Valid RegistrationDTO request) throws MessagingException {
          // fetch roles
          var userRole = masterRepository.findByCategoryAndCategoryValue("USER_ROLE", "INTERVIEWER")
                    .orElseThrow(() -> {
                         String errorMessage = messageSource.getMessage("role.notfound", new Object[] { ADMIN_ROLE },
                                   Locale.getDefault());
                         return new RuntimeException(errorMessage);
                    });
          // fetch status
          var userStatus = masterRepository.findByCategoryAndCategoryValue("USER_STATUS", "ACTIVE")
                    .orElseThrow(() -> {
                         String errorMessage = messageSource.getMessage("status.notfound", new Object[] { "ACTIVE" },
                                   Locale.getDefault());
                         return new RuntimeException(errorMessage);
                    });
          // check if email is already registered
          if (userRepository.existsByEmail(request.getEmail())) {
               String errorMessage = messageSource.getMessage("message.exists", new Object[] { request.getEmail() },
                         Locale.getDefault());
               throw new RuntimeException(errorMessage);
          }
          // create user
          var admin = User.builder()
                    .fullname(request.getFirstname() + request.getLastname())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    // .department(userDepartment)
                    .roleId(userRole.getCategoryId())
                    .status(userStatus.getCategoryId())
                    .enabled(false)
                    .build();

          userRepository.save(admin);
          // send email
          sendValidationEmail(admin);
     }

     private void sendValidationEmail(User user) throws MessagingException {
          String newToken = generateAndSaveActivationToken(user);
          // send email
          Map<String, Object> props = new HashMap<>();
          props.put("username", user.getUsername());
          props.put("activationCode", newToken);
          emailService.sendMail(
               "Account activation",
               "minhkhoilenhat04@gmail.com",
               user.getEmail(),
               EmailTemplateName.ACTIVATE_ACCOUNT,
               props,
               false
          );
     }

     private String generateAndSaveActivationToken(User user) {
          String generatedToken = generateActivationCode(6);
          var token = EmailToken.builder()
                    .token(generatedToken)
                    .createdAt(LocalDateTime.now())
                    .expiredAt(LocalDateTime.now().plusMinutes(15))
                    .user(user)
                    .build();
          emailTokenRepository.save(token);
          return generatedToken;
     }

     private String generateActivationCode(int length) {
          String characters = "0123456789";
          StringBuilder codeBuilder = new StringBuilder();
          SecureRandom secureRandom = new SecureRandom();
          for (int i = 0; i < length; i++) {
               int randomIndex = secureRandom.nextInt(characters.length()); // 0 to 9
               codeBuilder.append(characters.charAt(randomIndex));
          }
          return codeBuilder.toString();
     }

     public JwtTokenDTO authenticate(LoginDTO request) {
          authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
          var user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> {
                         String errorMessage = messageSource.getMessage("user.notfound",
                                   new Object[] { request.getEmail() },
                                   Locale.getDefault());
                         return new UsernameNotFoundException(errorMessage);
                    });
          return jwtService.createAuthToken(user);
     }

     public void activateAccount(String token) throws MessagingException {
          // not found token in db -> should not send email again
          EmailToken storedToken = emailTokenRepository.findByToken(token)
                    .orElseThrow(() -> {
                         String errorMessage = messageSource.getMessage("token.notfound", new Object[] { token },
                                   Locale.getDefault());
                         return new RuntimeException(errorMessage);
                    });
          if (LocalDateTime.now().isAfter(storedToken.getExpiredAt())) {
               sendValidationEmail(storedToken.getUser());
               String errorMessage = messageSource.getMessage("token.invalid", new Object[] { token },
                         Locale.getDefault());
               throw new RuntimeException(errorMessage);
          }
          var user = userRepository.findById(storedToken.getUser().getId())
                    .orElseThrow(() -> {
                         String errorMessage = messageSource.getMessage("user.notfound",
                                   new Object[] { storedToken.getUser().getUsername() }, Locale.getDefault());
                         return new UsernameNotFoundException(errorMessage);
                    });
          user.setEnabled(true);
          userRepository.save(user);
          storedToken.setValidatedAt(LocalDateTime.now());
          emailTokenRepository.save(storedToken);
     }

     @Override
     public void logout(HttpServletRequest request, HttpServletResponse response, Authentication connectedUser) {
          final String refreshToken = JwtTokenUtils.extractTokenFromCookie(request, JwtName.REFRESH_TOKEN.getValue());
          User user = userRepository.findByRefreshToken(refreshToken).orElseThrow(() -> {
               String errorMessage = messageSource.getMessage("jwttoken.notfound", null, Locale.getDefault());
               return new ExpiredJwtException(null, null, errorMessage);
          });
          user.setRefreshToken(null);
          userRepository.save(user);

          Cookie accessCookie = new Cookie(JwtName.ACCESS_TOKEN.getValue(), null);
          accessCookie.setPath("/");
          accessCookie.setHttpOnly(true);
          accessCookie.setMaxAge(0);
          response.addCookie(accessCookie);

          Cookie refreshTokenCookie = new Cookie(JwtName.REFRESH_TOKEN.getValue(), null);
          refreshTokenCookie.setPath("/");
          refreshTokenCookie.setHttpOnly(true);
          refreshTokenCookie.setMaxAge(0);
          response.addCookie(refreshTokenCookie);

          SecurityContextHolder.clearContext();
     }
}
