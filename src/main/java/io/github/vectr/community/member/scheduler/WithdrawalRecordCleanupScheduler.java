package io.github.vectr.community.member.scheduler;

import io.github.vectr.community.member.service.withdrawalRecord.WithdrawalRecordCleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WithdrawalRecordCleanupScheduler {

    private final WithdrawalRecordCleanupService cleanupService;

    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
    public void deleteExpiredRecords() {
        log.info("만료된 탈퇴 기록 정리 작업 시작");

        cleanupService.deleteExpiredRecords();
    }
}
