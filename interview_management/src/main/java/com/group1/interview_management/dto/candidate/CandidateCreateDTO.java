package com.group1.interview_management.dto.candidate;

import com.group1.interview_management.common.age_validation.MinAge;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CandidateCreateDTO {

    private Integer id;

    @NotBlank(message = "{ME002}")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "{ME028}")
    @Size(max = 255, message = "{ME029}")
    private String fullName;

    @NotBlank(message = "{ME002}")
    @Email(message = "{ME009}")
    private String email;

    @NotNull(message = "{ME002}")
    private Integer gender;

    @NotNull(message = "{ME002}")
    private Integer position;

    @NotNull(message = "{ME002}")
    @Size(min = 1, message = "{ME002}")
    private List<String> skills;


    @NotNull(message = "{ME002}")
    private Integer status;

    @NotNull(message = "{ME002}")
    private Integer highestLevel;

    @NotNull(message = "{ME002}")
    private Integer recruiter;

    @Size(max = 255, message = "{ME030}")
    @NotBlank(message = "{ME002}")
    private String address;

    @NotBlank(message = "{ME002}")
    @Pattern(regexp = "^\\d{10,15}$|^$", message = "{ME031}")
    private String phoneNumber;

    @Size(max = 500, message = "{ME032}")
    private String note;

    @NotBlank(message = "{ME002}")
    private String cvLink;

    private String cvFileName;

    @NotNull(message = "{ME002}")
    @Max(value = 100, message = "{ME033}")
    private Integer yoe;

    @NotNull(message = "{ME002}")
    @MinAge(value = 18)
    private LocalDate dob;
}
