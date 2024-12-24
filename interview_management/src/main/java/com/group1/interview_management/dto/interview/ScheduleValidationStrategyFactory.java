package com.group1.interview_management.dto.interview;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.group1.interview_management.services.ScheduleValidationStrategy;
import com.group1.interview_management.services.impl.Interview.CreateScheduleValidationStrategy;
import com.group1.interview_management.services.impl.Interview.EditScheduleValidationStrategy;

@Component
public class ScheduleValidationStrategyFactory {
     private final Map<SelectedMode, ScheduleValidationStrategy> strategies;

     public ScheduleValidationStrategyFactory(CreateScheduleValidationStrategy createStrategy,
               EditScheduleValidationStrategy editStrategy) {
          strategies = Map.of(
                    SelectedMode.CREATE, createStrategy,
                    SelectedMode.EDIT, editStrategy);
     }

     public ScheduleValidationStrategy getStrategy(SelectedMode mode) {
          return strategies.get(mode);
     }

     public enum SelectedMode {
          CREATE,
          EDIT
     }
}
