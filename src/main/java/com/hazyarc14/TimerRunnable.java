package com.hazyarc14;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
public class TimerRunnable implements Runnable {

    public static final Logger log = LoggerFactory.getLogger(TimerRunnable.class);

    private Integer timerLength;
    private Long userId;

    public TimerRunnable(Integer timerLength, Long userId) {
        this.timerLength = timerLength;
        this.userId = userId;
    }

    @Override
    public void run() {
        log.info("timerLength: " + timerLength + " userId: " + userId);
    }

    @Scheduled(fixedDelay = 1000)
    private void updateMessage() {
        log.info("Updating Message");
        return;
    }

}
