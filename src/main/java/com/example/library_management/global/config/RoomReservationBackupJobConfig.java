package com.example.library_management.global.config;

import com.example.library_management.domain.roomReserve.entity.RoomReserve;
import com.example.library_management.domain.roomReserve.repository.RoomReserveRepository;
import com.example.library_management.domain.roomReserveBackup.entity.RoomReserveBackup;
import com.example.library_management.domain.roomReserveBackup.repository.RoomReserveBackupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RoomReservationBackupJobConfig {

    private final RoomReserveRepository roomReserveRepository;
    private final RoomReserveBackupRepository roomReserveBackupRepository;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    // 2 가지의 Step을 가진 Job을 설정
    @Bean
    public Job roomReservationBackupJob() {
        log.info("Starting roomReservationBackupJob");
        return new JobBuilder("roomReservationBackupJob", jobRepository)
                .start(backupRoomReservationsStep())
                //.next(deleteExpiredRoomReservationsStep())
                .build();
    }

    // 삭제하기 전 예약 데이터들을 백업하는 Step
    @Bean
    public Step backupRoomReservationsStep() {
        // SimpleStepBuilder를 사용해 Chunk 기반 Step 구성
        SimpleStepBuilder<RoomReserve, RoomReserveBackup> stepBuilder =
                new StepBuilder("backupRoomReservationsStep", jobRepository)
                        .chunk(2, transactionManager);

        return stepBuilder
                .reader(roomReservationReader())
                .processor(roomReservationProcessor())
                .writer(roomReservationBackupWriter())
                .build();
    }

    // 백업된 예약 데이터들을 삭제하는 Step
    @Bean
    public Step deleteExpiredRoomReservationsStep() {
        // 예약 삭제를 위해 ItemReader, ItemProcessor, ItemWriter 사용
        return new StepBuilder("deleteExpiredRoomReservationsStep", jobRepository)
                .<RoomReserve, RoomReserve>chunk(2, transactionManager)
                .reader(roomReservationReaderForDelete())  // 예약 읽기
                .processor(roomReservationProcessorForDelete()) // 예약 처리
                .writer(roomReservationDeleteWriter()) // 예약 삭제
                .build();
    }

    @Bean
    public ItemReader<RoomReserve> roomReservationReader() {
        List<RoomReserve> expiredReservations = roomReserveRepository.findExpiredReservations(LocalDateTime.now());
        if (expiredReservations.isEmpty()) {
            log.warn("현재 만료된 예약 데이터가 존재하지 않습니다.");
        }

        final Iterator<RoomReserve> iterator = expiredReservations.iterator();

        return new ItemReader<RoomReserve>() {
            @Override
            public RoomReserve read() throws Exception {
                if (iterator.hasNext()) {
                    RoomReserve roomReserve = iterator.next();
                    System.out.println("Reading RoomReserve: " + roomReserve);
                    return roomReserve;
                } else {
                    System.out.println("없음");
                    return null;
                }
            }
        };
    }

    @Bean
    public ItemProcessor<RoomReserve, RoomReserveBackup> roomReservationProcessor() {
        return new ItemProcessor<RoomReserve, RoomReserveBackup>() {
            @Override
            public RoomReserveBackup process(RoomReserve roomReserve) throws Exception {
                System.out.println("처리중 : "+roomReserve.toString());
                return RoomReserveBackup.from(roomReserve);  // from 메서드를 호출하여 RoomReserveBackup 생성
            }
        };
    }

    @Bean
    public ItemWriter<RoomReserveBackup> roomReservationBackupWriter() {
        return backups -> {
            if (!backups.isEmpty()) {
                log.info("현재 저장되어 있는 {} 개의 예약 데이터를 백업합니다.", backups.size());
//                for (RoomReserveBackup backup : backups) {
//                    log.info("백업 데이터 저장 중: {}", backup);
//                    RoomReserveBackup roomReserveBackup = roomReserveBackupRepository.save(backup);  // 하나씩 저장
//                    System.out.println("저장 완료된 백업 :"+roomReserveBackup);
//                }
                backups.forEach(backup -> log.info("백업 예약 데이터: {}", backup));
                roomReserveBackupRepository.saveAll(backups);
            } else {
                log.warn("백업 할 예약 데이터가 존재하지 않습니다.");
            }
        };
    }

    // 예약 데이터를 읽어오는 Reader (삭제용)
    @Bean
    public ItemReader<RoomReserve> roomReservationReaderForDelete() {
        List<RoomReserve> expiredReservations = roomReserveRepository.findExpiredReservations(LocalDateTime.now());
        Iterator<RoomReserve> iterator = expiredReservations.iterator();

        return () -> iterator.hasNext() ? iterator.next() : null;
    }

    // 예약 데이터를 처리하는 Processor (삭제용)
    @Bean
    public ItemProcessor<RoomReserve, RoomReserve> roomReservationProcessorForDelete() {
        return roomReserve -> roomReserve; // 단순히 삭제할 예약을 반환
    }

    // 예약을 삭제하는 Writer
    @Bean
    public ItemWriter<RoomReserve> roomReservationDeleteWriter() {
        return roomReserves -> roomReserveRepository.deleteAll(roomReserves);
    }

}
