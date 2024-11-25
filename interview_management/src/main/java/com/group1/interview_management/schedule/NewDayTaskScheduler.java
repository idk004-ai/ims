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

    // Chạy vào lúc 00:00 mỗi ngày
    @Scheduled(cron = "0 0 0 * * ?")
    public void executeAtMidnight() {
        // Thực hiện các công việc cần làm vào thời điểm bắt đầu một ngày mới
        jobService.changeStatusJob();
    }

}

