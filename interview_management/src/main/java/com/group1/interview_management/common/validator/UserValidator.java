package com.group1.interview_management.common.validator;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;
import com.group1.interview_management.common.RegexUtil;
import com.group1.interview_management.dto.UserDTO;
import com.group1.interview_management.services.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class UserValidator {
    private final UserService userService;
    private final MessageSource messageSource;

    public void validateEmail(UserDTO userDTO, Map<String, String> errors) {
        if (!userDTO.getEmail().matches(RegexUtil.EMAIL_REGEX)) {
            errors.put("email", messageSource.getMessage("email.invalidFormat", null, Locale.getDefault()));
        } else if (userService.emailExists(userDTO.getEmail())) {
            errors.put("email", messageSource.getMessage("email.exists", null, Locale.getDefault()));
        }
    }

    public void validatePhone(UserDTO userDTO, Map<String, String> errors) {
        if (!userDTO.getPhoneNo().matches(RegexUtil.PHONE_REGEX)) {
            errors.put("phoneNo", messageSource.getMessage("phone.invalidFormat", null, Locale.getDefault()));
        } else if (userService.phoneExists(userDTO.getPhoneNo())) {
            errors.put("phoneNo", messageSource.getMessage("phone.exists", null, Locale.getDefault()));
        }
    }

    public void validateDob(UserDTO userDTO, Map<String, String> errors) {
        if (userDTO.getDob() == null || userDTO.getDob().isAfter(LocalDate.now())) {
            errors.put("dob", messageSource.getMessage("ME010", null, Locale.getDefault()));
        }
    }

    public void checkRequiredFields(UserDTO userDTO, Map<String, String> errors) {
        Map<String, String> fieldMappings = Map.of(
                "fullname", userDTO.getFullname(),
                "role", userDTO.getRole(),
                "status", userDTO.getStatus(),
                "address", userDTO.getAddress(),
                "gender", userDTO.getGender(),
                "department", userDTO.getDepartment()
        );

        String errorMessage = messageSource.getMessage("ME002", null, Locale.getDefault());
        fieldMappings.forEach((field, value) -> {
            if (Objects.requireNonNullElse(value, "").trim().isEmpty()) {
                errors.put(field, errorMessage);
            }
        });
    }
}
