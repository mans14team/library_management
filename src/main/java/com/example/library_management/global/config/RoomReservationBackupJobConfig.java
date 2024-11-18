package com.example.library_management.global.config;

import com.example.library_management.domain.roomReserve.dto.RoomReserveCsvDto;
import com.example.library_management.domain.roomReserve.entity.RoomReserve;
import com.example.library_management.domain.roomReserve.repository.RoomReserveRepository;
import com.example.library_management.domain.roomReserveBackup.repository.RoomReserveBackupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

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

    // 2가지의 Step을 가진 Job을 설정
    @Bean
    public Job roomReservationBackupJob() {
        log.info("Starting roomReservationBackupJob");
        return new JobBuilder("roomReservationBackupJob", jobRepository)
                .start(exportRoomReservationsToCsvStep())
                .next(deleteRoomReservationsStep())
                .build();
    }

    // RoomReserve 데이터를 CSV 파일로 내보내는 Step
    @Bean
    public Step exportRoomReservationsToCsvStep() {
        return new StepBuilder("exportRoomReservationsToCsvStep", jobRepository)
                .<RoomReserve, RoomReserveCsvDto>chunk(10, transactionManager)
                .reader(roomReservationReader())              // RoomReserve 엔티티 읽기
                .processor(roomReserveToCsvProcessor())       // RoomReserve -> RoomReserveCsvDto 변환
                .writer(roomReservationCsvFileWriter())               // RoomReserveCsvDto를 CSV 파일에 쓰기
                .build();
    }

    // RoomReserve 데이터를 삭제하는 Step
    @Bean
    public Step deleteRoomReservationsStep() {
        return new StepBuilder("deleteRoomReservationsStep", jobRepository)
                .<RoomReserve, RoomReserve>chunk(10, transactionManager)
                .reader(roomReservationReaderForDelete())
                .writer(roomReservationDeleteWriter())
                .build();
    }

    // RoomReserve 테이블의 만료된 예약 데이터를 읽어오는 Reader
    @Bean
    public ItemReader<RoomReserve> roomReservationReader() {
        List<RoomReserve> expiredReservations = roomReserveRepository.findExpiredReservations(LocalDateTime.now());
        log.info("만료된 예약 데이터 {}건을 읽어왔습니다.", expiredReservations.size());

        return new IteratorItemReader<>(expiredReservations);
    }

    // RoomReserve 자체를 바로 파일화 하기에는 연관관계의 id값을 가져오는데에 문제가 있음.
    @Bean
    public ItemProcessor<RoomReserve, RoomReserveCsvDto> roomReserveToCsvProcessor() {
        return roomReserve -> {
            RoomReserveCsvDto dto = new RoomReserveCsvDto(roomReserve);
            log.info("Processed RoomReserve to RoomReserveCsvDto: {}", dto);
            return dto;
        };
    }

    // 만료된 RoomReserve 데이터를 CSV 파일로 내보내는 Writer
    @Bean
    public FlatFileItemWriter<RoomReserveCsvDto> roomReservationCsvFileWriter() {
        return new FlatFileItemWriterBuilder<RoomReserveCsvDto>()
                .name("roomReservationCsvFileWriter")
                .resource(new FileSystemResource("backup/room_reserve_backup.csv"))
                .delimited()
                .delimiter(",")
                .names("userId", "roomId", "reservationDate", "reservationDateEnd", "backup_timeStamp")
                .append(true)  // 이어서 쓰기 설정
                .build();
    }

    // 삭제할 예약 데이터를 읽어오는 Reader
    @Bean
    public ItemReader<RoomReserve> roomReservationReaderForDelete() {
        List<RoomReserve> expiredReservations = roomReserveRepository.findExpiredReservations(LocalDateTime.now());
        log.info("삭제 대상 예약 데이터 {}건을 읽어왔습니다.", expiredReservations.size());
        for (RoomReserve reserve : expiredReservations) {
            log.info("삭제 대상 예약 데이터: {}", reserve);
        }
        Iterator<RoomReserve> iterator = expiredReservations.iterator();

        return () -> iterator.hasNext() ? iterator.next() : null;
    }

    // RoomReserve 데이터를 삭제하는 Writer
    @Bean
    public ItemWriter<RoomReserve> roomReservationDeleteWriter() {
        return roomReserves -> {
            log.info("만료된 예약 데이터 {}건을 삭제합니다.", roomReserves.size());
            roomReserves.forEach(roomReserve -> log.info("삭제할 예약 데이터: {}", roomReserve));
            roomReserveRepository.deleteAll(roomReserves);
            log.info("삭제 작업이 완료되었습니다.");
        };
    }
}
