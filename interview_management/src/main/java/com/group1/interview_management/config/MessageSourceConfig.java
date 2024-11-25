package com.group1.interview_management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class MessageSourceConfig {
     private final MessageSourceBean messageSourceBean;

     @Bean
     public LocalValidatorFactoryBean getValidator() {
          LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
          bean.setValidationMessageSource(messageSourceBean.messageSource());
          return bean;
     }
}
