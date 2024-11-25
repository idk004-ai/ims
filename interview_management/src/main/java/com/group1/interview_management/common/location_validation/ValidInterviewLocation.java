package com.group1.interview_management.common.location_validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = {
          com.group1.interview_management.common.location_validation.InterviewLocationValidator.class
})
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidInterviewLocation {
     String message() default "{time.invalid}";

     Class<?>[] groups() default {};

     Class<? extends Payload>[] payload() default {};
}
