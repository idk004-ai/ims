package com.group1.interview_management.common.validator;

import java.time.LocalDate;
import java.time.Period;
import java.util.Map;
import java.util.Objects;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

import com.group1.interview_management.common.ConstantUtils;
import com.group1.interview_management.common.RegexUtil;
import com.group1.interview_management.dto.UserDTO;
import com.group1.interview_management.services.CandidateService;
import com.group1.interview_management.services.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class UserValidator {
    private final UserService userService;
    private final CandidateService candidateService;
    private final MessageSource messageSource;

    public void validateName(UserDTO userDTO, Map<String, String> errors) {
        if (userDTO.getFullname().trim().matches(RegexUtil.INVALID_NAME_REGEX)) {
            errors.put("fullname", messageSource.getMessage("MEU004", null, Locale.getDefault()));
        }
    }

    public void validateEmail(UserDTO userDTO, Map<String, String> errors) {
        if (!userDTO.getEmail().trim().matches(RegexUtil.EMAIL_REGEX)) {
            errors.put("email", messageSource.getMessage("email.invalidFormat", null, Locale.getDefault()));
        } else if (userService.emailExists(userDTO.getEmail()) || candidateService.existsByEmail(userDTO.getEmail())) {
            errors.put("email", messageSource.getMessage("email.exists", null, Locale.getDefault()));
        }
    }

    public void validatePhone(UserDTO userDTO, Map<String, String> errors) {
        if (!userDTO.getPhoneNo().trim().matches(RegexUtil.PHONE_REGEX)) {
            errors.put("phoneNo", messageSource.getMessage("phone.invalidFormat", null, Locale.getDefault()));
        } else if (userService.phoneExists(userDTO.getPhoneNo()) || candidateService.existsByPhoneNumber(userDTO.getPhoneNo())) {
            errors.put("phoneNo", messageSource.getMessage("phone.exists", null, Locale.getDefault()));
        }
    }

    public void validateDob(UserDTO userDTO, Map<String, String> errors) {
        System.out.println(userDTO.getGender());
        System.out.println(calculateAge(userDTO.getDob()));
        if (userDTO.getDob() == null || userDTO.getDob().isAfter(LocalDate.now())) {
            errors.put("dob", messageSource.getMessage("ME010", null, Locale.getDefault()));
        }
        if (userDTO.getGender().equals(ConstantUtils.USER_GENDER_MALE_CATEGORY_ID) &&
                (calculateAge(userDTO.getDob()) < 18 ||
                        calculateAge(userDTO.getDob()) > 62)) {
            errors.put("dob", messageSource.getMessage("MEU002", null, Locale.getDefault()));
        }
        if (userDTO.getGender().equals(ConstantUtils.USER_GENDER_FEMALE_CATEGORY_ID) &&
                (calculateAge(userDTO.getDob()) < 18 ||
                        calculateAge(userDTO.getDob()) > 60)) {
            errors.put("dob", messageSource.getMessage("MEU003", null, Locale.getDefault()));
        }
        if (userDTO.getGender().equals(ConstantUtils.USER_GENDER_OTHER_CATEGORY_ID) &&
                (calculateAge(userDTO.getDob()) < 18 ||
                        calculateAge(userDTO.getDob()) > 60)) {
            errors.put("dob", messageSource.getMessage("MEU005", null, Locale.getDefault()));
        }
    }

    public void checkRequiredFields(UserDTO userDTO, Map<String, String> errors) {
        Map<String, String> fieldMappings = Map.of(
                "fullname", userDTO.getFullname(),
                "role", userDTO.getRole(),
                "status", userDTO.getStatus(),
                "address", userDTO.getAddress(),
                "gender", userDTO.getGender(),
                "department", userDTO.getDepartment(),
                "note", userDTO.getNote());
        String requiredErrorMessage = messageSource.getMessage("ME002", null, Locale.getDefault());
        String lengthErrorMessage = messageSource.getMessage("MEU006", null, Locale.getDefault());
        Map<String, int[]> lengthConstraints = Map.of(
                "fullname", new int[]{1, 255}, 
                "role", new int[]{1, 255},
                "status", new int[]{1, 255},
                "address", new int[]{1, 255},
                "gender", new int[]{1, 255},
                "department", new int[]{1, 255},
                "note", new int[]{0, 255});
        fieldMappings.forEach((field, value) -> {
            String trimmedValue = Objects.requireNonNullElse(value, "").trim();
            if (trimmedValue.isEmpty() && !field.equals("note")) {
                errors.put(field, requiredErrorMessage);
            } else {
                int[] constraints = lengthConstraints.getOrDefault(field, new int[]{0, Integer.MAX_VALUE});
                int min = constraints[0];
                int max = constraints[1];
                if (trimmedValue.length() < min || trimmedValue.length() > max) {
                    errors.put(field, lengthErrorMessage);
                }
            }
        });
    }
    

    public static int calculateAge(LocalDate birthDate) {
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthDate, currentDate).getYears();
    }

}
