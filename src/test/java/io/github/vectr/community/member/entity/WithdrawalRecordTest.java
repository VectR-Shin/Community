
package io.github.vectr.community.member.entity;

import io.github.vectr.community.member.enums.MemberProvider;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class WithdrawalRecordTest {

    @Test
    void constructor_success() {
        // given
        String emailHash = "email-hash";
        MemberProvider provider = MemberProvider.GOOGLE;
        String providerSubHash = "provider-sub-hash";
        Instant blockedUntil = Instant.parse("2026-12-17T03:00:00Z");

        // when
        WithdrawalRecord record = new WithdrawalRecord(
                emailHash,
                provider,
                providerSubHash,
                blockedUntil
        );

        // then
        assertThat(record.getEmailHash()).isEqualTo(emailHash);
        assertThat(record.getProvider()).isEqualTo(provider);
        assertThat(record.getProviderSubHash()).isEqualTo(providerSubHash);
        assertThat(record.getBlockedUntil()).isEqualTo(blockedUntil);
    }

    @Test
    void constructor_nullEmailHash() {
        // when & then
        assertThatNullPointerException()
                .isThrownBy(() -> new WithdrawalRecord(
                        null,
                        MemberProvider.GOOGLE,
                        "provider-sub-hash",
                        Instant.now()
                ));
    }

    @Test
    void constructor_nullProvider() {
        // when & then
        assertThatNullPointerException()
                .isThrownBy(() -> new WithdrawalRecord(
                        "email-hash",
                        null,
                        "provider-sub-hash",
                        Instant.now()
                ));
    }

    @Test
    void constructor_nullProviderSubHash() {
        // when & then
        assertThatNullPointerException()
                .isThrownBy(() -> new WithdrawalRecord(
                        "email-hash",
                        MemberProvider.GOOGLE,
                        null,
                        Instant.now()
                ));
    }

    @Test
    void constructor_nullBlockedUntil() {
        // when & then
        assertThatNullPointerException()
                .isThrownBy(() -> new WithdrawalRecord(
                        "email-hash",
                        MemberProvider.GOOGLE,
                        "provider-sub-hash",
                        null
                ));
    }

    @Test
    void isRestrictionActive_activeRestriction() {
        // given
        Instant now = Instant.parse("2026-11-17T03:00:00Z");
        Instant blockedUntil = now.plusSeconds(3600);

        WithdrawalRecord record = createWithdrawalRecord(blockedUntil);

        // when
        boolean result = record.isRestrictionActive(now);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void isRestrictionActive_expiredRestriction() {
        // given
        Instant now = Instant.parse("2026-11-17T03:00:00Z");
        Instant blockedUntil = now.minusSeconds(3600);

        WithdrawalRecord record = createWithdrawalRecord(blockedUntil);

        // when
        boolean result = record.isRestrictionActive(now);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void isRestrictionActive_restrictionExpiresAtNow() {
        // given
        Instant now = Instant.parse("2026-11-17T03:00:00Z");

        WithdrawalRecord record = createWithdrawalRecord(now);

        // when
        boolean result = record.isRestrictionActive(now);

        // then
        assertThat(result).isFalse();
    }

    private WithdrawalRecord createWithdrawalRecord(Instant blockedUntil) {
        return new WithdrawalRecord(
                "email-hash",
                MemberProvider.GOOGLE,
                "provider-sub-hash",
                blockedUntil
        );
    }
}
