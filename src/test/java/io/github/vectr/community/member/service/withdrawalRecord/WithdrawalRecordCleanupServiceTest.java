package io.github.vectr.community.member.service.withdrawalRecord;

import io.github.vectr.community.member.repository.WithdrawalRecordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WithdrawalRecordCleanupServiceTest {

    @Mock
    private WithdrawalRecordRepository withdrawalRecordRepository;

    @InjectMocks
    private WithdrawalRecordCleanupService cleanupService;

    @Test
    @DisplayName("만료된 탈퇴 기록을 삭제한다.")
    void deleteExpiredRecords() {
        // when
        cleanupService.deleteExpiredRecords();

        // then
        verify(withdrawalRecordRepository).deleteExpiredRecords(any(Instant.class));
    }
}