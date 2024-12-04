package com.group1.interview_management.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.group1.interview_management.common.ConstantUtils;
import com.group1.interview_management.common.EmailTemplateName;
import com.group1.interview_management.common.JwtName;
import com.group1.interview_management.common.JwtTokenUtils;
import com.group1.interview_management.common.LinkUtil;
import com.group1.interview_management.dto.JwtTokenDTO;
import com.group1.interview_management.dto.LoginDTO;
import com.group1.interview_management.dto.RegistrationDTO;
import com.group1.interview_management.dto.UserDTO;
import com.group1.interview_management.dto.JobDTO.response.ApiResponse;
import com.group1.interview_management.entities.User;
import com.group1.interview_management.services.CacheRequestService;
import com.group1.interview_management.services.EmailPeriodService;
import com.group1.interview_management.services.MasterService;
import com.group1.interview_management.services.UserService;
import com.group1.interview_management.services.impl.AuthenticationService;
import com.group1.interview_management.services.impl.EmailService;
import com.group1.interview_management.services.impl.JwtService;

import io.jsonwebtoken.ExpiredJwtException;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final MessageSource messageSource;
    private final EmailService emailService;
    private final EmailPeriodService emailPeriodService;
    private final MasterService masterService;
    private final UserService userService;
    private final CacheRequestService cacheRequestService;
    @Value("${jwt.refresh-token.path}")
    private String jwtRefreshTokenPath;
    @Value("${jwt.expiration}")
    private Integer jwtExpiration;
    @Value("${jwt.refresh-token.expiration}")
    private Integer refreshExpiration;

    @GetMapping("/login")
    public String showLoginPage(HttpServletRequest request, Model model) {
        return "auth/login";
    }

    @PostMapping("/register")
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register(@RequestBody @Valid RegistrationDTO request) throws MessagingException {
        authenticationService.register(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/authenticate")
    @ResponseBody
    public ResponseEntity<?> authenticate(@Valid @RequestBody LoginDTO loginDTO,
            HttpServletRequest request, HttpServletResponse response) {
        try {
            JwtTokenDTO jwtToken = authenticationService.authenticate(loginDTO);
            if (jwtToken == null) {
                String errorMessage = messageSource.getMessage("authenticate.invalid", null, Locale.getDefault());
                throw new Exception(errorMessage);
            }

            // Set JWT token in cookie
            jwtService.setTokenInsideCookie(response, jwtToken);
            String defaultRedirectUrl = "/api/v1/home";
            String cahedRequest = cacheRequestService.getAndRemoveCachedRequest(request.getSession().getId());
            String redirectUrl = cahedRequest != null ? cahedRequest : defaultRedirectUrl;

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "redirectUrl", redirectUrl));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.builder().message(e.getMessage()));
        }
    }

    @GetMapping("/activate-account")
    @ResponseBody
    public void confirm(
            @RequestParam String token) throws MessagingException {
        authenticationService.activateAccount(token);
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException, ExpiredJwtException {
        try {
            String refreshToken = JwtTokenUtils.extractTokenFromCookie(request, JwtName.REFRESH_TOKEN.getValue());
            if (refreshToken == null) {
                return ResponseEntity.badRequest().build();
            }
            JwtTokenDTO authResponse = jwtService.refreshToken(refreshToken);
            jwtService.setTokenInsideCookie(response, authResponse);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw ExpiredJwtException.class.cast(e);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> postMethodName(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) {
        authenticationService.logout(request, response, authentication);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        model.addAttribute("User", User.builder().build());
        return "forgot_password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@ModelAttribute User user, Model model, RedirectAttributes redirectAttributes)
            throws MessagingException {
        try {
            UserDTO userDTO = userService.getUserByEmail(user.getEmail());
            String informMessage = messageSource.getMessage("email.linksent", null, Locale.getDefault());
            String uuid = UUID.randomUUID().toString();
            if (masterService.findByCategoryAndValue(ConstantUtils.USER_STATUS, userDTO.getStatus()).get()
                    .getCategoryValue().equals(ConstantUtils.USER_ACTIVE)) {
                Map<String, Object> props = new HashMap<>();
                props.put("email", user.getEmail());
                props.put("URL", LinkUtil.generateLink(uuid));
                emailService.sendMail(ConstantUtils.PASSWORD_RESET, EmailService.DEFAULT_SENDER, user.getEmail(),
                        EmailTemplateName.RESET_PASSWORD_EMAIL, props, false);
                emailPeriodService.addResetPasswordRequest(LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                        userDTO.getId(), false, uuid);
                redirectAttributes.addFlashAttribute("message", informMessage);
                redirectAttributes.addFlashAttribute("flag", true);
            } else {
                redirectAttributes.addFlashAttribute("message",
                        messageSource.getMessage("MEU001", null, Locale.getDefault()));
                redirectAttributes.addFlashAttribute("flag", false);
            }
        } catch (RuntimeException e) {
            String errorMessage = messageSource.getMessage("ME005", null, Locale.getDefault());
            redirectAttributes.addFlashAttribute("message", errorMessage);
            redirectAttributes.addFlashAttribute("flag", false);
        }
        return "redirect:/auth/forgot-password";
    }

}
