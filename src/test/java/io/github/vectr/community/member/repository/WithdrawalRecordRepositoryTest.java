
package io.github.vectr.community.member.repository;

import io.github.vectr.community.common.config.JpaAuditingTestConfig;
import io.github.vectr.community.member.entity.WithdrawalRecord;
import io.github.vectr.community.member.enums.MemberProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import(JpaAuditingTestConfig.class)
class WithdrawalRecordRepositoryTest {

    @Autowired
    private WithdrawalRecordRepository withdrawalRecordRepository;

    @Test
    @DisplayName("이메일 해시와 재가입 제한 기간이 일치하는 탈퇴 기록이 존재하면 true를 반환한다.")
    void existsByEmailHashAndBlockedUntilAfter_existingRecord() {
        // given
        String emailHash = "email-hash";
        Instant now = Instant.now();

        saveWithdrawalRecord(
                emailHash,
                MemberProvider.GOOGLE,
                "provider-sub-hash",
                now.plusSeconds(3600)
        );

        // when
        boolean result = withdrawalRecordRepository
                .existsByEmailHashAndBlockedUntilAfter(emailHash, now);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("일치하는 이메일 해시의 탈퇴 기록이 없으면 false를 반환한다.")
    void existsByEmailHashAndBlockedUntilAfter_nonExistingRecord() {
        // given
        String emailHash = "non-existing-hash";
        Instant now = Instant.now();

        // when
        boolean result = withdrawalRecordRepository
                .existsByEmailHashAndBlockedUntilAfter(emailHash, now);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("이메일 해시가 일치하더라도 재가입 제한 기간이 만료되면 false를 반환한다.")
    void existsByEmailHashAndBlockedUntilAfter_expiredRecord() {
        // given
        String emailHash = "email-hash";
        Instant now = Instant.now();

        saveWithdrawalRecord(
                emailHash,
                MemberProvider.GOOGLE,
                "provider-sub-hash",
                now.minusSeconds(3600)
        );

        // when
        boolean result = withdrawalRecordRepository
                .existsByEmailHashAndBlockedUntilAfter(emailHash, now);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("인증 제공자와 Provider Sub 해시가 일치하는 탈퇴 기록이 존재하면 true를 반환한다.")
    void existsByProviderAndProviderSubHashAndBlockedUntilAfter_existingRecord() {
        // given
        MemberProvider provider = MemberProvider.GOOGLE;
        String providerSubHash = "provider-sub-hash";
        Instant now = Instant.now();

        saveWithdrawalRecord(
                "email-hash",
                provider,
                providerSubHash,
                now.plusSeconds(3600)
        );

        // when
        boolean result = withdrawalRecordRepository
                .existsByProviderAndProviderSubHashAndBlockedUntilAfter(
                        provider, providerSubHash, now
                );

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("인증 제공자가 일치하지 않으면 false를 반환한다.")
    void existsByProviderAndProviderSubHashAndBlockedUntilAfter_nonExistingRecord() {
        // given
        Instant now = Instant.now();

        saveWithdrawalRecord(
                "email-hash",
                MemberProvider.GOOGLE,
                "provider-sub-hash",
                now.plusSeconds(3600)
        );

        // when
        boolean result = withdrawalRecordRepository
                .existsByProviderAndProviderSubHashAndBlockedUntilAfter(
                        MemberProvider.KAKAO, "provider-sub-hash", now
                );

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Provider Sub 해시가 일치하더라도 재가입 제한 기간이 만료되면 false를 반환한다.")
    void existsByProviderAndProviderSubHashAndBlockedUntilAfter_expiredRecord() {
        // given
        MemberProvider provider = MemberProvider.GOOGLE;
        String providerSubHash = "provider-sub-hash";
        Instant now = Instant.now();

        saveWithdrawalRecord(
                "email-hash",
                provider,
                providerSubHash,
                now.minusSeconds(3600)
        );

        // when
        boolean result = withdrawalRecordRepository
                .existsByProviderAndProviderSubHashAndBlockedUntilAfter(
                        provider, providerSubHash, now
                );

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("만료된 탈퇴 기록만 삭제한다.")
    void deleteExpiredRecords() {
        // given
        Instant now = Instant.now();

        WithdrawalRecord expiredRecord = saveWithdrawalRecord(
                "expired-email-hash",
                MemberProvider.GOOGLE,
                "expired-provider-sub-hash",
                now.minusSeconds(3600)
        );

        WithdrawalRecord boundaryRecord = saveWithdrawalRecord(
                "boundary-email-hash",
                MemberProvider.KAKAO,
                "boundary-provider-sub-hash",
                now
        );

        WithdrawalRecord validRecord = saveWithdrawalRecord(
                "valid-email-hash",
                MemberProvider.NAVER,
                "valid-provider-sub-hash",
                now.plusSeconds(3600)
        );

        withdrawalRecordRepository.flush();

        // when
        int deletedCount = withdrawalRecordRepository.deleteExpiredRecords(now);

        // then
        assertThat(deletedCount).isEqualTo(2);

        assertThat(withdrawalRecordRepository.findAll())
                .containsExactly(validRecord);
    }

    private WithdrawalRecord saveWithdrawalRecord(
            String emailHash,
            MemberProvider provider,
            String providerSubHash,
            Instant blockedUntil
    ) {
        WithdrawalRecord record = new WithdrawalRecord(
                emailHash,
                provider,
                providerSubHash,
                blockedUntil
        );

        return withdrawalRecordRepository.save(record);
    }
}
