package com.group1.interview_management.schedule;

import com.group1.interview_management.services.JobService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class NewDayTaskScheduler {

    JobService jobService;

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Ho_Chi_Minh")
    public void executeAtMidnight() {
        jobService.changeStatusJob();
    }

}

