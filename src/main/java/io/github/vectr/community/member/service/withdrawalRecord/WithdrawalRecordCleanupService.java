package io.github.vectr.community.member.service.withdrawalRecord;

import io.github.vectr.community.member.repository.WithdrawalRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawalRecordCleanupService {

    private final WithdrawalRecordRepository withdrawalRecordRepository;

    @Transactional
    public void deleteExpiredRecords() {
        Instant now = Instant.now();

        int deletedCount =
                withdrawalRecordRepository.deleteExpiredRecords(now);

        log.info("만료된 탈퇴 기록 {}건 삭제 완료", deletedCount);
    }
}
