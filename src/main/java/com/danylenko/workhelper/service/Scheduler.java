package com.danylenko.workhelper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Scheduler {
    private final ProzorroServiceImpl notificationService;
    //@Scheduled(cron = "0 0 11 * * ?")
    //@Scheduled(cron = "0 */2 * * * ?")

    @Scheduled(cron = "0 */5 * * * ?")
    public void checkApiResponse() {
        notificationService.checkApiPayload();
    }
}
