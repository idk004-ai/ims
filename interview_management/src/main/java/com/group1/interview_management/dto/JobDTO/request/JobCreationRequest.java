package com.group1.interview_management.dto.JobDTO.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.PropertySource;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@PropertySource("classpath:messages.properties")
public class JobCreationRequest {

    Integer jobId;

    @NotNull(message = "{ME002}")
    @NotBlank(message = "{ME002}")
    @Size(max = 100, message = "{ME049}")
    String title;

    @NotNull(message = "{ME002}")
    @Min(value = 1000000, message = "{ME038}")
    @Max(value = 1000000000, message = "{ME038}")
    Double salaryFrom;

    @NotNull(message = "{ME002}")
    @Min(value = 1000000, message = "{ME038}")
    @Max(value = 1000000000, message = "{ME038}")
    Double salaryTo;

    @NotNull(message = "{ME002}")
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate startDate;

    @NotNull(message = "{ME002}")
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate endDate;

    @NotNull(message = "{ME002}")
    @NotEmpty(message = "{ME002}")
    List<String> level;

    @NotNull(message = "{ME002}")
    @NotEmpty(message = "{ME002}")
    List<String> benefits;

    @NotNull(message = "{ME002}")
    @NotEmpty(message = "{ME002}")
    List<String> skills;

    @Size(max = 500, message = "{ME049.1}")
    String workingAddress;
    @Size(max = 500, message = "{ME049.1}")
    String description;
    String statusJob;
}
