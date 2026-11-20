package io.github.vectr.community.member.scheduler;

import io.github.vectr.community.member.service.withdrawalRecord.WithdrawalRecordCleanupService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WithdrawalRecordCleanupSchedulerTest {

    @Mock
    private WithdrawalRecordCleanupService cleanupService;

    @InjectMocks
    private WithdrawalRecordCleanupScheduler scheduler;

    @Test
    @DisplayName("만료된 탈퇴 기록 정리 서비스를 호출한다.")
    void deleteExpiredRecords() {
        // when
        scheduler.deleteExpiredRecords();

        // then
        verify(cleanupService).deleteExpiredRecords();
    }
}