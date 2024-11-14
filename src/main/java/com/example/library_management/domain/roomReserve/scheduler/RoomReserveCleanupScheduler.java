package com.example.library_management.domain.roomReserve.scheduler;

import com.example.library_management.domain.roomReserve.repository.RoomReserveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class RoomReserveCleanupScheduler {
    private final JobLauncher jobLauncher;
    private final Job roomReservationBackupJob;

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void scheduleBackupJob() {
        try {
            // 새로운 JobParameters를 매번 생성하여 실행
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())  // 고유한 JobParameter를 추가
                    .toJobParameters();
            jobLauncher.run(roomReservationBackupJob, jobParameters);
            log.info("스터디룸 예약 백업 작업이 성공적으로 실행되었습니다.");
        } catch (Exception e) {
            log.error("스터디룸 예약 백업 작업 실행 중 오류 발생: ", e);
        }
    }
}
